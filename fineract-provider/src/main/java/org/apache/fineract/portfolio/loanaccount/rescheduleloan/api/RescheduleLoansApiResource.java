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
package org.apache.fineract.portfolio.loanaccount.rescheduleloan.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashSet;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.RescheduleLoansApiConstants;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.data.LoanRescheduleRequestData;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.service.LoanReschedulePreviewPlatformService;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.service.LoanRescheduleRequestReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/rescheduleloans")
@Component
@Scope("singleton")
@Tag(name = "Reschedule Loans", description = "")
public class RescheduleLoansApiResource {

    private final DefaultToApiJsonSerializer<LoanRescheduleRequestData> loanRescheduleRequestToApiJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanScheduleData> loanRescheduleToApiJsonSerializer;
    private final PlatformSecurityContext platformSecurityContext;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanRescheduleRequestReadPlatformService loanRescheduleRequestReadPlatformService;
    private final LoanReschedulePreviewPlatformService loanReschedulePreviewPlatformService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public RescheduleLoansApiResource(final DefaultToApiJsonSerializer<LoanRescheduleRequestData> loanRescheduleRequestToApiJsonSerializer,
            final PlatformSecurityContext platformSecurityContext,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final LoanRescheduleRequestReadPlatformService loanRescheduleRequestReadPlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper, final LoanReadPlatformService loanReadPlatformService,
            final DefaultToApiJsonSerializer<LoanScheduleData> loanRescheduleToApiJsonSerializer,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final LoanReschedulePreviewPlatformService loanReschedulePreviewPlatformService) {
        this.loanRescheduleRequestToApiJsonSerializer = loanRescheduleRequestToApiJsonSerializer;
        this.platformSecurityContext = platformSecurityContext;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.loanRescheduleRequestReadPlatformService = loanRescheduleRequestReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.loanRescheduleToApiJsonSerializer = loanRescheduleToApiJsonSerializer;
        this.loanReschedulePreviewPlatformService = loanReschedulePreviewPlatformService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(RescheduleLoansApiConstants.ENTITY_NAME);
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(queryParameters);

        String loanId = queryParameters.getFirst("loanId");
        LoanRescheduleRequestData loanRescheduleReasons = null;
        LoanTransactionData loanTransactionData = this.loanReadPlatformService.retrieveLoanTransactionTemplate(Long.valueOf(loanId));
        loanRescheduleReasons = this.loanRescheduleRequestReadPlatformService
                .retrieveAllRescheduleReasons(RescheduleLoansApiConstants.LOAN_RESCHEDULE_REASON);

        loanRescheduleReasons.updateLoanTransactionData(loanTransactionData);

        return this.loanRescheduleRequestToApiJsonSerializer.serialize(settings, loanRescheduleReasons);
    }

    @GET
    @Path("{scheduleId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String readLoanRescheduleRequest(@Context final UriInfo uriInfo, @PathParam("scheduleId") final Long scheduleId,
            @QueryParam("command") final String command) {
        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(RescheduleLoansApiConstants.ENTITY_NAME);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (compareIgnoreCase(command, "previewLoanReschedule")) {
            final LoanScheduleModel loanRescheduleModel = this.loanReschedulePreviewPlatformService.previewLoanReschedule(scheduleId);

            return this.loanRescheduleToApiJsonSerializer.serialize(settings, loanRescheduleModel.toData(), new HashSet<String>());
        }

        final LoanRescheduleRequestData loanRescheduleRequestData = this.loanRescheduleRequestReadPlatformService
                .readLoanRescheduleRequest(scheduleId);

        return this.loanRescheduleRequestToApiJsonSerializer.serialize(settings, loanRescheduleRequestData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createLoanRescheduleRequest(final String apiRequestBodyAsJson) {
        final CommandWrapper commandWrapper = new CommandWrapperBuilder()
                .createLoanRescheduleRequest(RescheduleLoansApiConstants.ENTITY_NAME).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);

        return this.loanRescheduleRequestToApiJsonSerializer.serialize(commandProcessingResult);
    }

    @POST
    @Path("{scheduleId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoanRescheduleRequest(@PathParam("scheduleId") final Long scheduleId, @QueryParam("command") final String command,
            final String apiRequestBodyAsJson) {
        CommandWrapper commandWrapper = null;

        if (compareIgnoreCase(command, "approve")) {
            commandWrapper = new CommandWrapperBuilder().approveLoanRescheduleRequest(RescheduleLoansApiConstants.ENTITY_NAME, scheduleId)
                    .withJson(apiRequestBodyAsJson).build();
        }

        else if (compareIgnoreCase(command, "reject")) {
            commandWrapper = new CommandWrapperBuilder().rejectLoanRescheduleRequest(RescheduleLoansApiConstants.ENTITY_NAME, scheduleId)
                    .withJson(apiRequestBodyAsJson).build();
        }

        else {
            throw new UnrecognizedQueryParamException("command", command, new Object[] { "approve", "reject" });
        }

        final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);

        return this.loanRescheduleRequestToApiJsonSerializer.serialize(commandProcessingResult);
    }

    /**
     * Compares two strings, ignoring differences in case
     *
     * @param firstString
     *            the first string
     * @param secondString
     *            the second string
     * @return true if the two strings are equal, else false
     **/
    private boolean compareIgnoreCase(String firstString, String secondString) {
        return StringUtils.isNotBlank(firstString) && firstString.trim().equalsIgnoreCase(secondString);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllRescheduleRequest(@Context final UriInfo uriInfo, @QueryParam("command") final String command) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(RescheduleLoansApiConstants.ENTITY_NAME);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (StringUtils.isNotBlank(command) && !RescheduleLoansApiConstants.commandParams.contains(command.toLowerCase())) {
            throw new UnrecognizedQueryParamException("command", command,
                    new Object[] { RescheduleLoansApiConstants.allCommandParamName, RescheduleLoansApiConstants.pendingCommandParamName,
                            RescheduleLoansApiConstants.approveCommandParamName, RescheduleLoansApiConstants.rejectCommandParamName });
        }
        final List<LoanRescheduleRequestData> loanRescheduleRequestsData = this.loanRescheduleRequestReadPlatformService
                .retrieveAllRescheduleRequests(command);

        return this.loanRescheduleRequestToApiJsonSerializer.serialize(settings, loanRescheduleRequestsData);
    }

    /*
     * @GET
     *
     * @Path("{scheduleId}")
     *
     * @Consumes({ MediaType.APPLICATION_JSON })
     *
     * @Produces({ MediaType.APPLICATION_JSON }) public String retrieveTemplate(@Context final UriInfo uriInfo) {
     *
     * this.platformSecurityContext.authenticatedUser().
     * validateHasReadPermission(RescheduleLoansApiConstants.ENTITY_NAME); final ApiRequestJsonSerializationSettings
     * settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
     *
     * LoanRescheduleRequestData loanRescheduleReasons = null; loanRescheduleReasons =
     * this.loanRescheduleRequestReadPlatformService .retrieveAllRescheduleReasons(RescheduleLoansApiConstants.
     * LOAN_RESCHEDULE_REASON);
     *
     * return this.loanRescheduleRequestToApiJsonSerializer.serialize(settings, loanRescheduleReasons); }
     */

    @PUT
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String undoLoanReschedule(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {
        CommandWrapper commandWrapper = new CommandWrapperBuilder().undoLoanReschedule(RescheduleLoansApiConstants.ENTITY_NAME, loanId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);

        return this.loanRescheduleRequestToApiJsonSerializer.serialize(commandProcessingResult);
    }
}
