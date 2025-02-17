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
package org.apache.fineract.portfolio.loanproduct.service;

import static org.apache.fineract.portfolio.interestratechart.InterestRateChartApiConstants.deleteParamName;
import static org.apache.fineract.portfolio.interestratechart.InterestRateChartApiConstants.idParamName;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.accounting.producttoaccountmapping.service.ProductToGLAccountMappingWritePlatformService;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityAccessType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.businessevent.domain.loan.product.LoanProductCreateBusinessEvent;
import org.apache.fineract.portfolio.businessevent.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.floatingrates.domain.FloatingRate;
import org.apache.fineract.portfolio.floatingrates.domain.FloatingRateRepositoryWrapper;
import org.apache.fineract.portfolio.fund.domain.Fund;
import org.apache.fineract.portfolio.fund.domain.FundRepository;
import org.apache.fineract.portfolio.fund.exception.FundNotFoundException;
import org.apache.fineract.portfolio.interestratechart.InterestRateChartApiConstants;
import org.apache.fineract.portfolio.interestratechart.domain.InterestRateChart;
import org.apache.fineract.portfolio.interestratechart.service.InterestRateChartAssembler;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionProcessingStrategyRepository;
import org.apache.fineract.portfolio.loanaccount.exception.LoanTransactionProcessingStrategyNotFoundException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.AprCalculator;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.domain.LoanTransactionProcessingStrategy;
import org.apache.fineract.portfolio.loanproduct.exception.InvalidCurrencyException;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductCannotBeModifiedDueToNonClosedLoansException;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductDateException;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.apache.fineract.portfolio.loanproduct.serialization.LoanProductDataValidator;
import org.apache.fineract.portfolio.rate.domain.Rate;
import org.apache.fineract.portfolio.rate.domain.RateRepositoryWrapper;
import org.apache.fineract.portfolio.savings.DepositsApiConstants;
import org.apache.fineract.portfolio.savings.domain.DepositProductAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanProductWritePlatformServiceJpaRepositoryImpl implements LoanProductWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(LoanProductWritePlatformServiceJpaRepositoryImpl.class);
    private final PlatformSecurityContext context;
    private final LoanProductDataValidator fromApiJsonDeserializer;
    private final LoanProductRepository loanProductRepository;
    private final AprCalculator aprCalculator;
    private final FundRepository fundRepository;
    private final LoanTransactionProcessingStrategyRepository loanTransactionProcessingStrategyRepository;
    private final ChargeRepositoryWrapper chargeRepository;
    private final RateRepositoryWrapper rateRepository;
    private final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService;
    private final FineractEntityAccessUtil fineractEntityAccessUtil;
    private final FloatingRateRepositoryWrapper floatingRateRepository;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final BusinessEventNotifierService businessEventNotifierService;

    private final DepositProductAssembler depositProductAssembler;

    private final InterestRateChartAssembler chartAssembler;

    private final CodeValueRepositoryWrapper codeValueRepository;

    @Autowired
    public LoanProductWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final LoanProductDataValidator fromApiJsonDeserializer, final LoanProductRepository loanProductRepository,
            final AprCalculator aprCalculator, final FundRepository fundRepository,
            final LoanTransactionProcessingStrategyRepository loanTransactionProcessingStrategyRepository,
            final ChargeRepositoryWrapper chargeRepository, final RateRepositoryWrapper rateRepository,
            final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService,
            final FineractEntityAccessUtil fineractEntityAccessUtil, final FloatingRateRepositoryWrapper floatingRateRepository,
            final LoanRepositoryWrapper loanRepositoryWrapper, final BusinessEventNotifierService businessEventNotifierService,
            DepositProductAssembler depositProductAssembler, InterestRateChartAssembler chartAssembler,
            CodeValueRepositoryWrapper codeValueRepository) {
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanProductRepository = loanProductRepository;
        this.aprCalculator = aprCalculator;
        this.fundRepository = fundRepository;
        this.loanTransactionProcessingStrategyRepository = loanTransactionProcessingStrategyRepository;
        this.chargeRepository = chargeRepository;
        this.rateRepository = rateRepository;
        this.accountMappingWritePlatformService = accountMappingWritePlatformService;
        this.fineractEntityAccessUtil = fineractEntityAccessUtil;
        this.floatingRateRepository = floatingRateRepository;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.businessEventNotifierService = businessEventNotifierService;
        this.depositProductAssembler = depositProductAssembler;
        this.chartAssembler = chartAssembler;
        this.codeValueRepository = codeValueRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createLoanProduct(final JsonCommand command) {

        try {

            this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command.json());
            validateInputDates(command);
            final String currencyCode = command.stringValueOfParameterNamed("currencyCode");
            final Fund fund = findFundByIdIfProvided(command.longValueOfParameterNamed("fundId"));

            final Long transactionProcessingStrategyId = command.longValueOfParameterNamed("transactionProcessingStrategyId");
            final LoanTransactionProcessingStrategy loanTransactionProcessingStrategy = findStrategyByIdIfProvided(
                    transactionProcessingStrategyId);

            final String shortName = command.stringValueOfParameterNamed(LoanProductConstants.SHORT_NAME);
            LoanProduct loanP = loanProductRepository.retrieveLoanProductsShortName(shortName);
            if (loanP != null) {
                throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.short.name",
                        "Loan product with short name `" + shortName + "` already exists", "shortName", shortName);
            }

            final List<Charge> charges = assembleListOfProductCharges(command, currencyCode);
            final List<Rate> rates = assembleListOfProductRates(command);

            FloatingRate floatingRate = null;
            if (command.parameterExists("floatingRatesId")) {
                floatingRate = this.floatingRateRepository
                        .findOneWithNotFoundDetection(command.longValueOfParameterNamed("floatingRatesId"));
            }

            CodeValue productCategory = getLoanProductCategory(command);
            CodeValue productType = getLoanProductType(command);
            final LoanProduct loanProduct = LoanProduct.assembleFromJson(fund, loanTransactionProcessingStrategy, charges, command,
                    this.aprCalculator, floatingRate, rates, productCategory, productType);
            loanProduct.updateLoanProductInRelatedClasses();
            // Interest rate charts
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanproduct");
            final Set<InterestRateChart> charts = this.depositProductAssembler.assembleListOfCharts(command,
                    loanProduct.getCurrency().getCode(), baseDataValidator);
            loanProduct.setCharts(charts);

            this.loanProductRepository.saveAndFlush(loanProduct);

            // save accounting mappings
            this.accountMappingWritePlatformService.createLoanProductToGLAccountMapping(loanProduct.getId(), command);
            // check if the office specific products are enabled. If yes, then
            // save this savings product against a specific office
            // i.e. this savings product is specific for this office.
            fineractEntityAccessUtil.checkConfigurationAndAddProductResrictionsForUserOffice(
                    FineractEntityAccessType.OFFICE_ACCESS_TO_LOAN_PRODUCTS, loanProduct.getId());

            businessEventNotifierService.notifyPostBusinessEvent(new LoanProductCreateBusinessEvent(loanProduct));

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanProduct.getId()) //
                    .build();

        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }

    }

    private LoanTransactionProcessingStrategy findStrategyByIdIfProvided(final Long transactionProcessingStrategyId) {
        LoanTransactionProcessingStrategy strategy = null;
        if (transactionProcessingStrategyId != null) {
            return this.loanTransactionProcessingStrategyRepository.findById(transactionProcessingStrategyId)
                    .orElseThrow(() -> new LoanTransactionProcessingStrategyNotFoundException(transactionProcessingStrategyId));
        }
        return strategy;
    }

    private Fund findFundByIdIfProvided(final Long fundId) {
        Fund fund = null;
        if (fundId != null) {
            fund = this.fundRepository.findById(fundId).orElseThrow(() -> new FundNotFoundException(fundId));
        }
        return fund;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateLoanProduct(final Long loanProductId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final LoanProduct product = this.loanProductRepository.findById(loanProductId)
                    .orElseThrow(() -> new LoanProductNotFoundException(loanProductId));

            this.fromApiJsonDeserializer.validateForUpdate(command.json(), product);
            validateInputDates(command);

            if (anyChangeInCriticalFloatingRateLinkedParams(command, product)
                    && this.loanRepositoryWrapper.doNonClosedLoanAccountsExistForProduct(product.getId())) {
                throw new LoanProductCannotBeModifiedDueToNonClosedLoansException(product.getId());
            }

            FloatingRate floatingRate = null;
            if (command.parameterExists("floatingRatesId")) {
                floatingRate = this.floatingRateRepository
                        .findOneWithNotFoundDetection(command.longValueOfParameterNamed("floatingRatesId"));
            }

            final Map<String, Object> changes = product.update(command, this.aprCalculator, floatingRate);
            this.updateCharts(command, changes, product);
            if (changes.containsKey("fundId")) {
                final Long fundId = (Long) changes.get("fundId");
                final Fund fund = findFundByIdIfProvided(fundId);
                product.update(fund);
            }

            if (changes.containsKey("transactionProcessingStrategyId")) {
                final Long transactionProcessingStrategyId = (Long) changes.get("transactionProcessingStrategyId");
                final LoanTransactionProcessingStrategy loanTransactionProcessingStrategy = findStrategyByIdIfProvided(
                        transactionProcessingStrategyId);
                product.update(loanTransactionProcessingStrategy);
            }

            if (changes.containsKey("charges")) {
                final List<Charge> productCharges = assembleListOfProductCharges(command, product.getCurrency().getCode());
                final boolean updated = product.update(productCharges);
                if (!updated) {
                    changes.remove("charges");
                }
            }

            // accounting related changes
            final boolean accountingTypeChanged = changes.containsKey("accountingRule");
            final Map<String, Object> accountingMappingChanges = this.accountMappingWritePlatformService
                    .updateLoanProductToGLAccountMapping(product.getId(), command, accountingTypeChanged, product.getAccountingType());
            changes.putAll(accountingMappingChanges);

            if (changes.containsKey(LoanProductConstants.RATES_PARAM_NAME)) {
                final List<Rate> productRates = assembleListOfProductRates(command);
                final boolean updated = product.updateRates(productRates);
                if (!updated) {
                    changes.remove(LoanProductConstants.RATES_PARAM_NAME);
                }
            }

            if (changes.containsKey(LoanProductConstants.LOAN_PRODUCT_CATEGORY)) {
                final Long categoryId = (Long) changes.get(LoanProductConstants.LOAN_PRODUCT_CATEGORY);
                CodeValue val = null;
                if (categoryId != null) val = this.codeValueRepository.findOneWithNotFoundDetection(categoryId);
                product.setProductCategory(val);
            }

            if (changes.containsKey(LoanProductConstants.LOAN_PRODUCT_TYPE)) {
                final Long typeId = (Long) changes.get(LoanProductConstants.LOAN_PRODUCT_TYPE);
                CodeValue val = null;
                if (typeId != null) val = this.codeValueRepository.findOneWithNotFoundDetection(typeId);
                product.setProductType(val);
            }

            if (!changes.isEmpty()) {
                product.validateLoanProductPreSave();
                this.loanProductRepository.saveAndFlush(product);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanProductId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException | JpaSystemException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }

    }

    private boolean anyChangeInCriticalFloatingRateLinkedParams(JsonCommand command, LoanProduct product) {
        final boolean isChangeFromFloatingToFlatOrViceVersa = command.isChangeInBooleanParameterNamed("isLinkedToFloatingInterestRates",
                product.isLinkedToFloatingInterestRate());
        final boolean isChangeInCriticalFloatingRateParams = product.getFloatingRates() != null
                && (command.isChangeInLongParameterNamed("floatingRatesId", product.getFloatingRates().getFloatingRate().getId())
                        || command.isChangeInBigDecimalParameterNamed("interestRateDifferential",
                                product.getFloatingRates().getInterestRateDifferential()));
        return isChangeFromFloatingToFlatOrViceVersa || isChangeInCriticalFloatingRateParams;
    }

    private List<Charge> assembleListOfProductCharges(final JsonCommand command, final String currencyCode) {

        final List<Charge> charges = new ArrayList<>();

        String loanProductCurrencyCode = command.stringValueOfParameterNamed("currencyCode");
        if (loanProductCurrencyCode == null) {
            loanProductCurrencyCode = currencyCode;
        }

        if (command.parameterExists("charges")) {
            final JsonArray chargesArray = command.arrayOfParameterNamed("charges");
            if (chargesArray != null) {
                for (int i = 0; i < chargesArray.size(); i++) {

                    final JsonObject jsonObject = chargesArray.get(i).getAsJsonObject();
                    if (jsonObject.has("id")) {
                        final Long id = jsonObject.get("id").getAsLong();

                        final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(id);

                        if (!loanProductCurrencyCode.equals(charge.getCurrencyCode())) {
                            final String errorMessage = "Charge and Loan Product must have the same currency.";
                            throw new InvalidCurrencyException("charge", "attach.to.loan.product", errorMessage);
                        }
                        charges.add(charge);
                    }
                }
            }
        }

        return charges;
    }

    private List<Rate> assembleListOfProductRates(final JsonCommand command) {

        final List<Rate> rates = new ArrayList<>();

        if (command.parameterExists("rates")) {
            final JsonArray ratesArray = command.arrayOfParameterNamed("rates");
            if (ratesArray != null) {
                List<Long> idList = new ArrayList<>();
                for (int i = 0; i < ratesArray.size(); i++) {

                    final JsonObject jsonObject = ratesArray.get(i).getAsJsonObject();
                    if (jsonObject.has("id")) {
                        idList.add(jsonObject.get("id").getAsLong());
                    }
                }
                rates.addAll(this.rateRepository.findMultipleWithNotFoundDetection(idList));
            }
        }

        return rates;
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("'external_id'")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.externalId",
                    "Loan Product with externalId `" + externalId + "` already exists", "externalId", externalId, realCause);
        } else if (realCause.getMessage().contains("'unq_name'")) {

            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.name",
                    "Loan product with name `" + name + "` already exists", "name", name, realCause);
        } else if (realCause.getMessage().contains("'unq_short_name'")) {

            final String shortName = command.stringValueOfParameterNamed("shortName");
            throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.short.name",
                    "Loan product with short name `" + shortName + "` already exists", "shortName", shortName, realCause);
        } else if (realCause.getMessage().contains("Duplicate entry")) {
            throw new PlatformDataIntegrityException("error.msg.product.loan.duplicate.charge",
                    "Loan product may only have one charge of each type.`", "charges", realCause);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.product.loan.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.", realCause);
    }

    private void validateInputDates(final JsonCommand command) {
        final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
        final LocalDate closeDate = command.localDateValueOfParameterNamed("closeDate");

        if (startDate != null && closeDate != null) {
            if (closeDate.isBefore(startDate)) {
                throw new LoanProductDateException(startDate.toString(), closeDate.toString());
            }
        }
    }

    private void logAsErrorUnexpectedDataIntegrityException(final Exception dve) {
        LOG.error("Error occured.", dve);
    }

    private void updateCharts(JsonCommand command, Map<String, Object> actualChanges, LoanProduct product) {
        final Map<String, Object> deletedCharts = new HashMap<>();
        final Map<String, Object> chartsChanges = new HashMap<>();

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanproduct");

        if (command.hasParameter(DepositsApiConstants.chartsParamName)) {
            final JsonArray array = command.arrayOfParameterNamed(DepositsApiConstants.chartsParamName);
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject chartElement = array.get(i).getAsJsonObject();
                    JsonCommand chartCommand = JsonCommand.fromExistingCommand(command, chartElement);
                    if (chartCommand.parameterExists(idParamName)) {
                        final Long chartId = chartCommand.longValueOfParameterNamed(idParamName);
                        final InterestRateChart chart = product.findChart(chartId);
                        if (chart == null) {
                            baseDataValidator.parameter(idParamName).value(chartId).failWithCode("no.chart.associated.with.id");
                        } else if (chartCommand.parameterExists(deleteParamName)) {
                            if (product.removeChart(chart)) {
                                deletedCharts.put(idParamName, chartId);
                            }
                        } else {
                            chart.update(chartCommand, chartsChanges, baseDataValidator, product.setOfCharts(),
                                    product.getCurrency().getCode());
                        }
                    } else {
                        // assemble chart
                        final InterestRateChart newChart = this.chartAssembler.assembleFrom(chartElement, product.getCurrency().getCode(),
                                baseDataValidator);
                        product.addChart(newChart);
                    }
                }
            }
        }

        // this.validateCharts(baseDataValidator);

        // add chart changes to actual changes list.
        if (!chartsChanges.isEmpty()) {
            actualChanges.put(InterestRateChartApiConstants.chartSlabs, chartsChanges);
        }

        // add deleted chart to actual changes
        if (!deletedCharts.isEmpty()) {
            actualChanges.put("deletedChartSlabs", deletedCharts);
        }

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

    @Nullable
    private CodeValue getLoanProductCategory(JsonCommand command) {
        CodeValue productCategory = null;
        final Long productCategoryId = command.longValueOfParameterNamed(LoanProductConstants.LOAN_PRODUCT_CATEGORY);
        if (productCategoryId != null) {
            productCategory = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.PRODUCT_CATEGORY,
                    productCategoryId);
        }
        return productCategory;
    }

    @Nullable
    private CodeValue getLoanProductType(JsonCommand command) {
        CodeValue productType = null;
        final Long productTypeId = command.longValueOfParameterNamed(LoanProductConstants.LOAN_PRODUCT_TYPE);
        if (productTypeId != null) {
            productType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.PRODUCT_TYPE,
                    productTypeId);
        }
        return productType;
    }
}
