/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanaccount.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformServiceImpl;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRecruitmentSurvey;
import org.apache.fineract.portfolio.client.domain.ClientRecruitmentSurveyRepository;
import org.apache.fineract.portfolio.common.domain.KivaLoanDepartmentThemeTypeMapper;
import org.apache.fineract.portfolio.loanaccount.data.KivaLoanAccount;
import org.apache.fineract.portfolio.loanaccount.data.KivaLoanAccountSchedule;
import org.apache.fineract.portfolio.loanaccount.data.LoanDetailToKivaData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.serialization.KivaDateSerializerApi;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class KivaLoanServiceImpl implements KivaLoanService {

    private static final Logger LOG = LoggerFactory.getLogger(KivaLoanServiceImpl.class);
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String FORM_URL_CONTENT_TYPE = "Content-Type";
    public static final Long ACTIVITY_ID = 110L;
    public static final Integer DESCRIPTION_LANGUAGE_ID = 1;

    private final LoanRepository loanRepository;
    private final DocumentReadPlatformServiceImpl documentReadPlatformService;
    private final ClientRecruitmentSurveyRepository clientRecruitmentSurveyRepository;
    @Autowired
    private Environment env;

    @Override
    @CronTarget(jobName = JobName.POST_LOAN_ACCOUNTS_TO_KIVA)
    public void postLoanAccountsToKiva() {
        // Authenticate to KIVA
        String accessToken = authenticateToKiva();

        List<KivaLoanAccountSchedule> kivaLoanAccountSchedules = new ArrayList<>();
        List<KivaLoanAccount> kivaLoanAccounts = new ArrayList<>();
        List<Boolean> notPictured = new ArrayList<>();
        notPictured.add(Boolean.TRUE);

        List<Loan> loanList = loanRepository.findLoanAccountsToBePostedToKiva();

        LOG.info("Posting this Loan Account To Kiva And Size = = > " + loanList.size());
        if (!CollectionUtils.isEmpty(loanList)) {
            for (Loan loan : loanList) {
                String loanToKiva = loanPayloadToKivaMapper(kivaLoanAccountSchedules, kivaLoanAccounts, notPictured, loan);
                LOG.info("Loan Account To be Sent to Kiva : =GSON = >  " + loanToKiva);
                String loanDraftUUID = postLoanToKiva(accessToken, loanToKiva);
                loan.setKivaUUId(loanDraftUUID);
                loanRepository.saveAndFlush(loan);
            }
        }
    }

    @Override
    @CronTarget(jobName = JobName.POST_LOAN_REPAYMENTS_TO_KIVA)
    public void postLoanRepaymentsToKiva() throws JobExecutionException {
        // Authenticate to KIVA
        String accessToken = authenticateToKiva();
        // Get Loan Accounts expecting repayment from KIVA
        JsonObject pingKivaForSizeOfLoanAwaitingRepayment = getLoanAccountsReadyForRepayments(accessToken);

    }

    private String loanPayloadToKivaMapper(List<KivaLoanAccountSchedule> kivaLoanAccountSchedules, List<KivaLoanAccount> kivaLoanAccounts,
            List<Boolean> notPictured, Loan loan) {

        Client client = loan.getClient();
        String gender = (client.gender() != null) ? client.gender().label().toLowerCase() : "unknown";
        String loanPurpose = (loan.getLoanPurpose() != null) ? loan.getLoanPurpose().label() : "Not Defined";
        String clientKivaId = (client.getExternalId() != null) ? client.getExternalId() : client.getId().toString();
        String base64Image = generateBase64Image(loan);

        ClientRecruitmentSurvey clientRecruitmentSurvey = clientRecruitmentSurveyRepository.getByClientId(client.getId());
        String location = getClientLocation(clientRecruitmentSurvey);

        KivaLoanAccount loanAccount = new KivaLoanAccount(loan.getNetDisbursalAmount(), clientKivaId, client.getFirstname(), gender,
                client.getLastname(), getLoanKivaId(loan));
        kivaLoanAccounts.add(loanAccount);

        for (LoanRepaymentScheduleInstallment scheduleInstallment : loan.getRepaymentScheduleInstallments()) {
            KivaLoanAccountSchedule schedule = new KivaLoanAccountSchedule(Date.valueOf(scheduleInstallment.getDueDate()),
                    scheduleInstallment.getInterestOutstanding(loan.getCurrency()).getAmount(),
                    scheduleInstallment.getPrincipal(loan.getCurrency()).getAmount());
            kivaLoanAccountSchedules.add(schedule);
        }

        // build final object
        LoanDetailToKivaData loanDetailToKivaData = new LoanDetailToKivaData(ACTIVITY_ID, Boolean.TRUE, loan.getCurrencyCode(),
                loan.getDescription(), DESCRIPTION_LANGUAGE_ID, Date.valueOf(loan.getDisbursementDate()), " ", base64Image,
                client.getId().toString(), generateInternalLoanId(loan.getDisbursementDate(), loan.getId()), loanPurpose, location,
                getKivaLoanDepartmentThemeType(loan), kivaLoanAccounts, kivaLoanAccountSchedules, notPictured);

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new KivaDateSerializerApi()).create();

        return gson.toJson(loanDetailToKivaData);
    }

    private String getClientLocation(ClientRecruitmentSurvey clientRecruitmentSurvey) {
        String clientLocation = (clientRecruitmentSurvey != null) ? clientRecruitmentSurvey.getSurveyLocation().label() : "Not Defined";
        String clientCountry = (clientRecruitmentSurvey != null) ? clientRecruitmentSurvey.getCountry().label() : "Not Defined";
        return clientLocation + ": " + clientCountry;
    }

    private String generateBase64Image(Loan loan) {
        final DocumentData documentData = this.documentReadPlatformService.retrieveKivaLoanProfileImage("loans", loan.getId());
        if (documentData != null && documentData.fileLocation() != null && documentData.storageType().equals(StorageType.FILE_SYSTEM)) {
            String formatIdentifier = null;

            if ("image/png".equalsIgnoreCase(documentData.contentType())) {
                formatIdentifier = "data:image/png;base64,";
            } else if ("image/jpg".equalsIgnoreCase(documentData.contentType())
                    || "image/jpeg".equalsIgnoreCase(documentData.contentType())) {
                formatIdentifier = "data:image/jpeg;base64,";
            } else if ("image/gif".equalsIgnoreCase(documentData.contentType())) {
                formatIdentifier = "data:image/gif;base64,";
            } else {
                throw new GeneralPlatformDomainRuleException("error.msg.unsupported.image.type.to.kiva",
                        "Only PNG,GIF,JPEG,JPG are accepted but this " + documentData.contentType() + " was found");
            }

            return formatIdentifier + getImageAsBase64(documentData.fileLocation());
        }
        return null;
    }

    @NotNull
    private static Integer getKivaLoanDepartmentThemeType(Loan loan) {
        return KivaLoanDepartmentThemeTypeMapper.toInt(loan.getDepartment().label()).intValue();
    }

    private String getLoanKivaId(Loan loan) {
        return (loan.getKivaId() != null) ? loan.getKivaId() : loan.getExternalId();
    }

    private String authenticateToKiva() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(getConfigProperty("fineract.integrations.kiva.oAuthUrl")).newBuilder();
        String url = urlBuilder.build().toString();

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("grant_type=" + getConfigProperty("fineract.integrations.kiva.grantType"));
        requestBody.append("&scope=" + getConfigProperty("fineract.integrations.kiva.scope"));
        requestBody.append("&audience=" + getConfigProperty("fineract.integrations.kiva.audience"));
        requestBody.append("&client_id=" + getConfigProperty("fineract.integrations.kiva.clientId"));
        requestBody.append("&client_secret=" + getConfigProperty("fineract.integrations.kiva.clientSecret"));

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        RequestBody formBody = RequestBody.create(MediaType.parse(FORM_URL_ENCODED), requestBody.toString());

        Request request = new Request.Builder().url(url).header(FORM_URL_CONTENT_TYPE, FORM_URL_ENCODED).post(formBody).build();

        List<Throwable> exceptions = new ArrayList<>();

        try {
            response = client.newCall(request).execute();
            String resObject = response.body().string();
            if (response.isSuccessful()) {

                JsonObject jsonResponse = JsonParser.parseString(resObject).getAsJsonObject();
                String accessToken = jsonResponse.get("access_token").getAsString();

                log.info("Login to KIVA is Successful");

                return accessToken;
            } else {
                log.error("Login to KIVA failed with Message:", resObject);

                handleAPIIntegrityIssues(resObject);

            }
        } catch (Exception e) {
            log.error("Authentication to KIVA has failed", e);
            exceptions.add(e);
        }
        if (!CollectionUtils.isEmpty(exceptions)) {
            try {
                throw new JobExecutionException(exceptions);
            } catch (JobExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void handleAPIIntegrityIssues(String httpResponse) {
        throw new PlatformDataIntegrityException(httpResponse, httpResponse);
    }

    private String getConfigProperty(String propertyName) {
        return this.env.getProperty(propertyName);
    }

    private String generateInternalLoanId(LocalDate disbursementDate, Long loanId) {
        Integer year = disbursementDate.getYear();
        return "LOAN/" + loanId + "/" + year;
    }

    private String postLoanToKiva(String accessToken, String loanToKiva) {
        HttpUrl urlBuilder = new HttpUrl.Builder().scheme(getConfigProperty("fineract.integrations.kiva.httpType"))
                .host(getConfigProperty("fineract.integrations.kiva.baseUrl"))
                .addPathSegment(getConfigProperty("fineract.integrations.kiva.apiVersion"))
                .addPathSegment(getConfigProperty("fineract.integrations.kiva.partnerCode"))
                .addPathSegment(getConfigProperty("fineract.integrations.kiva.partnerId")).addPathSegment("loan_draft").build();

        String url = urlBuilder.toString();

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        RequestBody formBody = RequestBody.create(MediaType.parse(FORM_URL_CONTENT_TYPE), loanToKiva);

        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).post(formBody).build();

        List<Throwable> exceptions = new ArrayList<>();

        try {
            response = client.newCall(request).execute();
            String resObject = response.body().string();
            if (response.isSuccessful()) {

                JsonObject jsonResponse = JsonParser.parseString(resObject).getAsJsonObject();
                log.info("Loan Account Response from KIVA :=>" + resObject);

                return jsonResponse.get("loan_draft_uuid").getAsString();

            } else {
                log.error("Post Loan to KIVA failed with Message:", resObject);

                handleAPIIntegrityIssues(resObject);

            }
        } catch (Exception e) {
            log.error("Post Loan to KIVA has failed", e);
            exceptions.add(e);
        }
        if (!CollectionUtils.isEmpty(exceptions)) {
            try {
                throw new JobExecutionException(exceptions);
            } catch (JobExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public String getImageAsBase64(String imageName) {
        File imageFile = new File(imageName);
        byte[] imageBytes;
        try {
            imageBytes = FileCopyUtils.copyToByteArray(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private JsonObject getLoanAccountsReadyForRepayments(String accessToken) {

        HttpUrl.Builder builder = new HttpUrl.Builder().scheme(getConfigProperty("fineract.integrations.kiva.httpType"))
                .host(getConfigProperty("fineract.integrations.kiva.baseUrl"))
                .addPathSegment(getConfigProperty("fineract.integrations.kiva.apiVersion"))
                .addPathSegment(getConfigProperty("fineract.integrations.kiva.partnerCode"))
                .addPathSegment(getConfigProperty("fineract.integrations.kiva.partnerId")).addPathSegment("loans");

        builder.addQueryParameter("limit", "1").addQueryParameter("offset", "0").addQueryParameter("status", "payingBack");

        String url = builder.build().toString();

        OkHttpClient client = new OkHttpClient();
        List<Throwable> exceptions = new ArrayList<>();
        Response response = null;

        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).get().build();

        try {
            response = client.newCall(request).execute();
            String resObject = response.body().string();
            if (response.isSuccessful()) {

                JsonObject jsonResponse = JsonParser.parseString(resObject).getAsJsonObject();
                log.info("Loan Account Expecting Repayment from KIVA :=>" + resObject);

                return jsonResponse;

            } else {
                log.error("Get Loans expecting repayment from KIVA failed with Message:", resObject);

                handleAPIIntegrityIssues(resObject);

            }
        } catch (Exception e) {
            log.error("Get Loans expecting repayment from KIVA failed", e);
            exceptions.add(e);
        }
        if (!CollectionUtils.isEmpty(exceptions)) {
            try {
                throw new JobExecutionException(exceptions);
            } catch (JobExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
