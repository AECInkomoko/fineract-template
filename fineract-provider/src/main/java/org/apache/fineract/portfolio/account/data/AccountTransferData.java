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
package org.apache.fineract.portfolio.account.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.client.data.ClientData;

/**
 * Immutable data object representing a savings account.
 */
@SuppressWarnings("unused")
public final class AccountTransferData implements Serializable {

    private final Long id;
    private final Boolean reversed;
    private final CurrencyData currency;
    private final BigDecimal transferAmount;
    private final LocalDate transferDate;
    private final String transferDescription;
    private final OfficeData fromOffice;
    private final ClientData fromClient;
    private final EnumOptionData fromAccountType;
    private final PortfolioAccountData fromAccount;
    private final OfficeData toOffice;
    private final ClientData toClient;
    private final EnumOptionData toAccountType;
    private final PortfolioAccountData toAccount;

    // template
    private final Collection<OfficeData> fromOfficeOptions;
    private final Collection<EnumOptionData> fromAccountTypeOptions;
    private final Collection<PortfolioAccountData> fromAccountOptions;
    private final Collection<OfficeData> toOfficeOptions;
    private final Collection<EnumOptionData> toAccountTypeOptions;
    private final Collection<PortfolioAccountData> toAccountOptions;

    public static AccountTransferData template(final OfficeData fromOffice, final ClientData fromClient,
            final EnumOptionData fromAccountType, final PortfolioAccountData fromAccount, final LocalDate transferDate,
            final OfficeData toOffice, final ClientData toClient, final EnumOptionData toAccountType, final PortfolioAccountData toAccount,
            final Collection<OfficeData> fromOfficeOptions, final Collection<EnumOptionData> fromAccountTypeOptions,
            final Collection<PortfolioAccountData> fromAccountOptions, final Collection<OfficeData> toOfficeOptions,
            final Collection<EnumOptionData> toAccountTypeOptions, final Collection<PortfolioAccountData> toAccountOptions) {
        final Long id = null;
        CurrencyData currency = null;
        BigDecimal transferAmount = BigDecimal.ZERO;
        if (fromAccount != null) {
            currency = fromAccount.currency();
            if (fromAccount.getAmtForTransfer() != null) {
                transferAmount = fromAccount.getAmtForTransfer();
            }
        }
        final String transferDescription = null;
        final Boolean reversed = null;
        return new AccountTransferData(id, reversed, fromOffice, fromClient, fromAccountType, fromAccount, currency, transferAmount,
                transferDate, transferDescription, toOffice, toClient, toAccountType, toAccount, fromOfficeOptions, fromAccountTypeOptions,
                fromAccountOptions, toOfficeOptions, toAccountTypeOptions, toAccountOptions);
    }

    public static AccountTransferData instance(final Long id, final Boolean reversed, final LocalDate transferDate,
            final CurrencyData currency, final BigDecimal transferAmount, final String transferDescription, final OfficeData fromOffice,
            final OfficeData toOffice, final ClientData fromClient, final ClientData toClient, final EnumOptionData fromAccountType,
            final PortfolioAccountData fromAccount, final EnumOptionData toAccountType, final PortfolioAccountData toAccount) {

        return new AccountTransferData(id, reversed, fromOffice, fromClient, fromAccountType, fromAccount, currency, transferAmount,
                transferDate, transferDescription, toOffice, toClient, toAccountType, toAccount, null, null, null, null, null, null);
    }

    public static AccountTransferData transferBasicDetails(final Long id, final CurrencyData currency, final BigDecimal transferAmount,
            final LocalDate transferDate, final String description, final Boolean reversed) {

        final EnumOptionData fromAccountType = null;
        final EnumOptionData toAccountType = null;

        return new AccountTransferData(id, reversed, null, null, fromAccountType, null, currency, transferAmount, transferDate, description,
                null, null, toAccountType, null, null, null, null, null, null, null);
    }

    private AccountTransferData(final Long id, final Boolean reversed, final OfficeData fromOffice, final ClientData fromClient,
            final EnumOptionData fromAccountType, final PortfolioAccountData fromAccount, final CurrencyData currency,
            final BigDecimal transferAmount, final LocalDate transferDate, final String transferDescription, final OfficeData toOffice,
            final ClientData toClient, final EnumOptionData toAccountType, final PortfolioAccountData toAccount,
            final Collection<OfficeData> fromOfficeOptions, final Collection<EnumOptionData> fromAccountTypeOptions,
            final Collection<PortfolioAccountData> fromAccountOptions, final Collection<OfficeData> toOfficeOptions,
            final Collection<EnumOptionData> toAccountTypeOptions, final Collection<PortfolioAccountData> toAccountOptions) {
        this.id = id;
        this.reversed = reversed;
        this.fromOffice = fromOffice;
        this.fromClient = fromClient;
        this.fromAccountType = fromAccountType;
        this.fromAccount = fromAccount;
        this.toOffice = toOffice;
        this.toClient = toClient;
        this.toAccountType = toAccountType;
        this.toAccount = toAccount;

        this.currency = currency;
        this.transferAmount = transferAmount;
        this.transferDate = transferDate;
        this.transferDescription = transferDescription;

        this.fromOfficeOptions = fromOfficeOptions;
        this.fromAccountTypeOptions = fromAccountTypeOptions;
        this.fromAccountOptions = fromAccountOptions;
        this.toOfficeOptions = toOfficeOptions;
        this.toAccountTypeOptions = toAccountTypeOptions;
        this.toAccountOptions = toAccountOptions;
    }
}
