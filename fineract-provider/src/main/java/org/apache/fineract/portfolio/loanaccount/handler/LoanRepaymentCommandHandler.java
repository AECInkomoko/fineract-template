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
package org.apache.fineract.portfolio.loanaccount.handler;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.DataIntegrityErrorHandler;
import org.apache.fineract.infrastructure.Odoo.OdooService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CommandType(entity = "LOAN", action = "REPAYMENT")
public class LoanRepaymentCommandHandler implements NewCommandSourceHandler {

    private final LoanWritePlatformService writePlatformService;
    private final DataIntegrityErrorHandler dataIntegrityErrorHandler;
    private final OdooService odooService;
    private final FromJsonHelper fromJsonHelper;

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        BigDecimal transactionAmount = this.fromJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", command.parsedJson());
        String transactionDate = this.fromJsonHelper.extractStringNamed("transactionDate", command.parsedJson());
        String note = this.fromJsonHelper.extractStringNamed("note", command.parsedJson());
        String paymentTypeId = this.fromJsonHelper.extractStringNamed("paymentTypeId", command.parsedJson());
        try {
            final boolean isRecoveryRepayment = false;
            final boolean isPayOff = false;
            return this.writePlatformService.makeLoanRepayment(LoanTransactionType.REPAYMENT, command.getLoanId(), command,
                    isRecoveryRepayment, isPayOff);
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            this.odooService.postFailedLoanRepaymentOnMigration(transactionAmount, command.getLoanId(), transactionDate, note,
                    paymentTypeId, dve.getMessage(), command.json());
            dataIntegrityErrorHandler.handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve, "loan.repayment", "Repayment");
            return CommandProcessingResult.empty();
        } catch (final Exception dve) {
            this.odooService.postFailedLoanRepaymentOnMigration(transactionAmount, command.getLoanId(), transactionDate, note,
                    paymentTypeId, dve.getMessage(), command.json());
            throw dve;
        }
    }
}
