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
package org.apache.fineract.portfolio.savings.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.producttoaccountmapping.data.ChargeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.PaymentTypeToGLAccountMapper;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.interestratechart.data.InterestRateChartData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;

/**
 * Immutable data object represent a Recurring Deposit product.
 */
public final class RecurringDepositProductData extends DepositProductData {

    private boolean preClosurePenalApplicable;
    private BigDecimal preClosurePenalInterest;
    private EnumOptionData preClosurePenalInterestOnType;
    private Integer minDepositTerm;
    private Integer maxDepositTerm;
    private EnumOptionData minDepositTermType;
    private EnumOptionData maxDepositTermType;
    private Integer inMultiplesOfDepositTerm;
    private EnumOptionData inMultiplesOfDepositTermType;
    private BigDecimal minDepositAmount;
    private BigDecimal depositAmount;
    private BigDecimal maxDepositAmount;
    private boolean isMandatoryDeposit;
    private boolean allowWithdrawal;
    private boolean adjustAdvanceTowardsFuturePayments;

    private Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions;
    private Collection<EnumOptionData> periodFrequencyTypeOptions;

    private boolean allowFreeWithdrawal;

    public static RecurringDepositProductData template(final CurrencyData currency, final EnumOptionData interestCompoundingPeriodType,
            final EnumOptionData interestPostingPeriodType, final EnumOptionData interestCalculationType,
            final EnumOptionData interestCalculationDaysInYearType, final EnumOptionData accountingRule,
            final Collection<CurrencyData> currencyOptions, final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions,
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions,
            final Collection<EnumOptionData> interestCalculationTypeOptions,
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions,
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions, final Collection<EnumOptionData> withdrawalFeeTypeOptions,
            final Collection<PaymentTypeData> paymentTypeOptions, final Collection<EnumOptionData> accountingRuleOptions,
            final Map<String, List<GLAccountData>> accountingMappingOptions, final Collection<ChargeData> chargeOptions,
            final Collection<ChargeData> penaltyOptions, final InterestRateChartData chartTemplate,
            final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions,
            final Collection<EnumOptionData> periodFrequencyTypeOptions, final Collection<TaxGroupData> taxGroupOptions,
            final List<CodeValueData> productCategories, final List<CodeValueData> productTypes) {

        final Long id = null;
        final String name = null;
        final String shortName = null;
        final String description = null;
        final BigDecimal nominalAnnualInterestRate = null;

        final Integer lockinPeriodFrequency = null;
        final EnumOptionData lockinPeriodFrequencyType = null;
        final BigDecimal minBalanceForInterestCalculation = null;

        final Map<String, Object> accountingMappings = null;
        final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;
        final Collection<ChargeData> charges = null;
        final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings = null;
        final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings = null;
        final Collection<InterestRateChartData> interestRateCharts = null;
        final boolean preClosurePenalApplicable = false;
        final BigDecimal preClosurePenalInterest = null;
        final EnumOptionData preClosurePenalInterestOnType = null;
        final boolean isMandatoryDeposit = false;
        final boolean allowWithdrawal = false;
        final boolean allowFreeWithdrawal = false;
        final boolean adjustAdvanceTowardsFuturePayments = false;
        final Integer minDepositTerm = null;
        final Integer maxDepositTerm = null;
        final EnumOptionData minDepositTermType = null;
        final EnumOptionData maxDepositTermType = null;
        final Integer inMultiplesOfDepositTerm = null;
        final EnumOptionData inMultiplesOfDepositTermType = null;
        final BigDecimal minDepositAmount = null;
        final BigDecimal depositAmount = null;
        final BigDecimal maxDepositAmount = null;
        final boolean withHoldTax = false;
        final boolean addPenaltyOnMissedTargetSavings = false;
        final TaxGroupData taxGroup = null;
        final Long productCategoryId = null;
        final Long productTypeId = null;

        return new RecurringDepositProductData(id, name, shortName, description, currency, nominalAnnualInterestRate,
                interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType,
                lockinPeriodFrequency, lockinPeriodFrequencyType, minBalanceForInterestCalculation, accountingRule, accountingMappings,
                paymentChannelToFundSourceMappings, currencyOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions,
                interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions,
                withdrawalFeeTypeOptions, paymentTypeOptions, accountingRuleOptions, accountingMappingOptions, charges, chargeOptions,
                penaltyOptions, feeToIncomeAccountMappings, penaltyToIncomeAccountMappings, interestRateCharts, chartTemplate,
                preClosurePenalApplicable, preClosurePenalInterest, preClosurePenalInterestOnType, preClosurePenalInterestOnTypeOptions,
                minDepositTerm, maxDepositTerm, minDepositTermType, maxDepositTermType, inMultiplesOfDepositTerm,
                inMultiplesOfDepositTermType, isMandatoryDeposit, allowWithdrawal, adjustAdvanceTowardsFuturePayments,
                periodFrequencyTypeOptions, minDepositAmount, depositAmount, maxDepositAmount, withHoldTax, taxGroup, taxGroupOptions,
                allowFreeWithdrawal, addPenaltyOnMissedTargetSavings, productCategories, productTypes, productCategoryId, productTypeId);
    }

    public static RecurringDepositProductData withCharges(final RecurringDepositProductData existingProduct,
            final Collection<ChargeData> charges) {
        return new RecurringDepositProductData(existingProduct.id, existingProduct.name, existingProduct.shortName,
                existingProduct.description, existingProduct.currency, existingProduct.nominalAnnualInterestRate,
                existingProduct.interestCompoundingPeriodType, existingProduct.interestPostingPeriodType,
                existingProduct.interestCalculationType, existingProduct.interestCalculationDaysInYearType,
                existingProduct.lockinPeriodFrequency, existingProduct.lockinPeriodFrequencyType,
                existingProduct.minBalanceForInterestCalculation, existingProduct.accountingRule, existingProduct.accountingMappings,
                existingProduct.paymentChannelToFundSourceMappings, existingProduct.currencyOptions,
                existingProduct.interestCompoundingPeriodTypeOptions, existingProduct.interestPostingPeriodTypeOptions,
                existingProduct.interestCalculationTypeOptions, existingProduct.interestCalculationDaysInYearTypeOptions,
                existingProduct.lockinPeriodFrequencyTypeOptions, existingProduct.withdrawalFeeTypeOptions,
                existingProduct.paymentTypeOptions, existingProduct.accountingRuleOptions, existingProduct.accountingMappingOptions,
                charges, existingProduct.chargeOptions, existingProduct.penaltyOptions, existingProduct.feeToIncomeAccountMappings,
                existingProduct.penaltyToIncomeAccountMappings, existingProduct.interestRateCharts, existingProduct.chartTemplate,
                existingProduct.preClosurePenalApplicable, existingProduct.preClosurePenalInterest,
                existingProduct.preClosurePenalInterestOnType, existingProduct.preClosurePenalInterestOnTypeOptions,
                existingProduct.minDepositTerm, existingProduct.maxDepositTerm, existingProduct.minDepositTermType,
                existingProduct.maxDepositTermType, existingProduct.inMultiplesOfDepositTerm, existingProduct.inMultiplesOfDepositTermType,
                existingProduct.isMandatoryDeposit, existingProduct.allowWithdrawal, existingProduct.adjustAdvanceTowardsFuturePayments,
                existingProduct.periodFrequencyTypeOptions, existingProduct.minDepositAmount, existingProduct.depositAmount,
                existingProduct.maxDepositAmount, existingProduct.withHoldTax, existingProduct.taxGroup, existingProduct.taxGroupOptions,
                existingProduct.allowFreeWithdrawal, existingProduct.addPenaltyOnMissedTargetSavings,
                existingProduct.getProductCategories(), existingProduct.getProductTypes(), existingProduct.getProductCategoryId(),
                existingProduct.getProductTypeId());
    }

    /**
     * Returns a {@link RecurringDepositProductData} that contains and exist {@link RecurringDepositProductData} data
     * with further template data for dropdowns.
     *
     * @param taxGroupOptions
     *            TODO
     */
    public static RecurringDepositProductData withTemplate(final RecurringDepositProductData existingProduct,
            final Collection<CurrencyData> currencyOptions, final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions,
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions,
            final Collection<EnumOptionData> interestCalculationTypeOptions,
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions,
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions, final Collection<EnumOptionData> withdrawalFeeTypeOptions,
            final Collection<PaymentTypeData> paymentTypeOptions, final Collection<EnumOptionData> accountingRuleOptions,
            final Map<String, List<GLAccountData>> accountingMappingOptions, final Collection<ChargeData> chargeOptions,
            final Collection<ChargeData> penaltyOptions, final InterestRateChartData chartTemplate,
            final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions,
            final Collection<EnumOptionData> periodFrequencyTypeOptions, final Collection<TaxGroupData> taxGroupOptions,
            final List<CodeValueData> productCategories, final List<CodeValueData> productTypes) {

        return new RecurringDepositProductData(existingProduct.id, existingProduct.name, existingProduct.shortName,
                existingProduct.description, existingProduct.currency, existingProduct.nominalAnnualInterestRate,
                existingProduct.interestCompoundingPeriodType, existingProduct.interestPostingPeriodType,
                existingProduct.interestCalculationType, existingProduct.interestCalculationDaysInYearType,
                existingProduct.lockinPeriodFrequency, existingProduct.lockinPeriodFrequencyType,
                existingProduct.minBalanceForInterestCalculation, existingProduct.accountingRule, existingProduct.accountingMappings,
                existingProduct.paymentChannelToFundSourceMappings, currencyOptions, interestCompoundingPeriodTypeOptions,
                interestPostingPeriodTypeOptions, interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions,
                lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions, accountingRuleOptions,
                accountingMappingOptions, existingProduct.charges, chargeOptions, penaltyOptions,
                existingProduct.feeToIncomeAccountMappings, existingProduct.penaltyToIncomeAccountMappings,
                existingProduct.interestRateCharts, chartTemplate, existingProduct.preClosurePenalApplicable,
                existingProduct.preClosurePenalInterest, existingProduct.preClosurePenalInterestOnType,
                preClosurePenalInterestOnTypeOptions, existingProduct.minDepositTerm, existingProduct.maxDepositTerm,
                existingProduct.minDepositTermType, existingProduct.maxDepositTermType, existingProduct.inMultiplesOfDepositTerm,
                existingProduct.inMultiplesOfDepositTermType, existingProduct.isMandatoryDeposit, existingProduct.allowWithdrawal,
                existingProduct.adjustAdvanceTowardsFuturePayments, periodFrequencyTypeOptions, existingProduct.minDepositAmount,
                existingProduct.depositAmount, existingProduct.maxDepositAmount, existingProduct.withHoldTax, existingProduct.taxGroup,
                taxGroupOptions, existingProduct.allowFreeWithdrawal, existingProduct.addPenaltyOnMissedTargetSavings, productCategories,
                productTypes, existingProduct.getProductCategoryId(), existingProduct.getProductTypeId());

    }

    public static RecurringDepositProductData withAccountingDetails(final RecurringDepositProductData existingProduct,
            final Map<String, Object> accountingMappings, final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings,
            final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings,
            final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings) {

        final Collection<CurrencyData> currencyOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        final Collection<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final Collection<ChargeData> penaltyOptions = null;

        return new RecurringDepositProductData(existingProduct.id, existingProduct.name, existingProduct.shortName,
                existingProduct.description, existingProduct.currency, existingProduct.nominalAnnualInterestRate,
                existingProduct.interestCompoundingPeriodType, existingProduct.interestPostingPeriodType,
                existingProduct.interestCalculationType, existingProduct.interestCalculationDaysInYearType,
                existingProduct.lockinPeriodFrequency, existingProduct.lockinPeriodFrequencyType,
                existingProduct.minBalanceForInterestCalculation, existingProduct.accountingRule, accountingMappings,
                paymentChannelToFundSourceMappings, currencyOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions,
                interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions,
                withdrawalFeeTypeOptions, paymentTypeOptions, accountingRuleOptions, accountingMappingOptions, existingProduct.charges,
                chargeOptions, penaltyOptions, feeToIncomeAccountMappings, penaltyToIncomeAccountMappings,
                existingProduct.interestRateCharts, existingProduct.chartTemplate, existingProduct.preClosurePenalApplicable,
                existingProduct.preClosurePenalInterest, existingProduct.preClosurePenalInterestOnType,
                existingProduct.preClosurePenalInterestOnTypeOptions, existingProduct.minDepositTerm, existingProduct.maxDepositTerm,
                existingProduct.minDepositTermType, existingProduct.maxDepositTermType, existingProduct.inMultiplesOfDepositTerm,
                existingProduct.inMultiplesOfDepositTermType, existingProduct.isMandatoryDeposit, existingProduct.allowWithdrawal,
                existingProduct.adjustAdvanceTowardsFuturePayments, existingProduct.periodFrequencyTypeOptions,
                existingProduct.minDepositAmount, existingProduct.depositAmount, existingProduct.maxDepositAmount,
                existingProduct.withHoldTax, existingProduct.taxGroup, existingProduct.taxGroupOptions, existingProduct.allowFreeWithdrawal,
                existingProduct.addPenaltyOnMissedTargetSavings, existingProduct.getProductCategories(), existingProduct.getProductTypes(),
                existingProduct.getProductCategoryId(), existingProduct.getProductTypeId());
    }

    public static RecurringDepositProductData instance(final DepositProductData depositProductData, final boolean preClosurePenalApplicable,
            final BigDecimal preClosurePenalInterest, final EnumOptionData preClosurePenalInterestOnType, final Integer minDepositTerm,
            final Integer maxDepositTerm, final EnumOptionData minDepositTermType, final EnumOptionData maxDepositTermType,
            final Integer inMultiplesOfDepositTerm, final EnumOptionData inMultiplesOfDepositTermType, final boolean isMandatoryDeposit,
            boolean allowWithdrawal, boolean adjustAdvanceTowardsFuturePayments, final BigDecimal minDepositAmount,
            final BigDecimal depositAmount, final BigDecimal maxDepositAmount, boolean allowFreeWithdrawal) {

        final Map<String, Object> accountingMappings = null;
        final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;

        final Collection<CurrencyData> currencyOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        final Collection<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final Collection<ChargeData> penaltyOptions = null;
        final Collection<ChargeData> charges = null;
        final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings = null;
        final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings = null;
        final Collection<InterestRateChartData> interestRateCharts = null;
        final InterestRateChartData chartTemplate = null;
        final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions = null;
        final Collection<EnumOptionData> periodFrequencyTypeOptions = null;
        final Collection<TaxGroupData> taxGroupOptions = null;

        return new RecurringDepositProductData(depositProductData.id, depositProductData.name, depositProductData.shortName,
                depositProductData.description, depositProductData.currency, depositProductData.nominalAnnualInterestRate,
                depositProductData.interestCompoundingPeriodType, depositProductData.interestPostingPeriodType,
                depositProductData.interestCalculationType, depositProductData.interestCalculationDaysInYearType,
                depositProductData.lockinPeriodFrequency, depositProductData.lockinPeriodFrequencyType,
                depositProductData.minBalanceForInterestCalculation, depositProductData.accountingRule, accountingMappings,
                paymentChannelToFundSourceMappings, currencyOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions,
                interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions,
                withdrawalFeeTypeOptions, paymentTypeOptions, accountingRuleOptions, accountingMappingOptions, charges, chargeOptions,
                penaltyOptions, feeToIncomeAccountMappings, penaltyToIncomeAccountMappings, interestRateCharts, chartTemplate,
                preClosurePenalApplicable, preClosurePenalInterest, preClosurePenalInterestOnType, preClosurePenalInterestOnTypeOptions,
                minDepositTerm, maxDepositTerm, minDepositTermType, maxDepositTermType, inMultiplesOfDepositTerm,
                inMultiplesOfDepositTermType, isMandatoryDeposit, allowWithdrawal, adjustAdvanceTowardsFuturePayments,
                periodFrequencyTypeOptions, minDepositAmount, depositAmount, maxDepositAmount, depositProductData.withHoldTax,
                depositProductData.taxGroup, taxGroupOptions, allowFreeWithdrawal, depositProductData.addPenaltyOnMissedTargetSavings,
                depositProductData.getProductCategories(), depositProductData.getProductTypes(), depositProductData.getProductCategoryId(),
                depositProductData.getProductTypeId());
    }

    public static RecurringDepositProductData lookup(final Long id, final String name) {

        final String shortName = null;
        final CurrencyData currency = null;
        final String description = null;
        final BigDecimal nominalAnnualInterestRate = null;
        final EnumOptionData interestCompoundingPeriodType = null;
        final EnumOptionData interestPostingPeriodType = null;
        final EnumOptionData interestCalculationType = null;
        final EnumOptionData interestCalculationDaysInYearType = null;

        final Integer lockinPeriodFrequency = null;
        final EnumOptionData lockinPeriodFrequencyType = null;
        final BigDecimal minBalanceForInterestCalculation = null;

        final EnumOptionData accountingType = null;
        final Map<String, Object> accountingMappings = null;
        final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;

        final Collection<CurrencyData> currencyOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        final Collection<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<ChargeData> charges = null;
        final Collection<ChargeData> chargeOptions = null;
        final Collection<ChargeData> penaltyOptions = null;
        final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings = null;
        final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings = null;
        final Collection<InterestRateChartData> interestRateCharts = null;
        final InterestRateChartData chartTemplate = null;
        final boolean preClosurePenalApplicable = false;
        final BigDecimal preClosurePenalInterest = null;
        final EnumOptionData preClosurePenalInterestOnType = null;
        final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions = null;
        final boolean isMandatoryDeposit = false;
        final boolean allowWithdrawal = false;
        final boolean allowFreeWithdrawal = false;
        final boolean adjustAdvanceTowardsFuturePayments = false;
        final Integer minDepositTerm = null;
        final Integer maxDepositTerm = null;
        final EnumOptionData minDepositTermType = null;
        final EnumOptionData maxDepositTermType = null;
        final Integer inMultiplesOfDepositTerm = null;
        final EnumOptionData inMultiplesOfDepositTermType = null;
        final Collection<EnumOptionData> periodFrequencyTypeOptions = null;
        final BigDecimal minDepositAmount = null;
        final BigDecimal depositAmount = null;
        final BigDecimal maxDepositAmount = null;
        final boolean withHoldTax = false;
        final boolean addPenaltyOnMissedTargetSavings = false;
        final TaxGroupData taxGroup = null;
        final Collection<TaxGroupData> taxGroupOptions = null;
        final List<CodeValueData> productCategories = null;
        final List<CodeValueData> productTypes = null;
        final Long productCategoryId = null;
        final Long productTypeId = null;

        return new RecurringDepositProductData(id, name, shortName, description, currency, nominalAnnualInterestRate,
                interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType,
                lockinPeriodFrequency, lockinPeriodFrequencyType, minBalanceForInterestCalculation, accountingType, accountingMappings,
                paymentChannelToFundSourceMappings, currencyOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions,
                interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions,
                withdrawalFeeTypeOptions, paymentTypeOptions, accountingRuleOptions, accountingMappingOptions, charges, chargeOptions,
                penaltyOptions, feeToIncomeAccountMappings, penaltyToIncomeAccountMappings, interestRateCharts, chartTemplate,
                preClosurePenalApplicable, preClosurePenalInterest, preClosurePenalInterestOnType, preClosurePenalInterestOnTypeOptions,
                minDepositTerm, maxDepositTerm, minDepositTermType, maxDepositTermType, inMultiplesOfDepositTerm,
                inMultiplesOfDepositTermType, isMandatoryDeposit, allowWithdrawal, adjustAdvanceTowardsFuturePayments,
                periodFrequencyTypeOptions, minDepositAmount, depositAmount, maxDepositAmount, withHoldTax, taxGroup, taxGroupOptions,
                allowFreeWithdrawal, addPenaltyOnMissedTargetSavings, productCategories, productTypes, productCategoryId, productTypeId);
    }

    public static RecurringDepositProductData withInterestChart(final RecurringDepositProductData product,
            final Collection<InterestRateChartData> interestRateCharts) {
        return new RecurringDepositProductData(product.id, product.name, product.shortName, product.description, product.currency,
                product.nominalAnnualInterestRate, product.interestCompoundingPeriodType, product.interestPostingPeriodType,
                product.interestCalculationType, product.interestCalculationDaysInYearType, product.lockinPeriodFrequency,
                product.lockinPeriodFrequencyType, product.minBalanceForInterestCalculation, product.accountingRule,
                product.accountingMappings, product.paymentChannelToFundSourceMappings, product.currencyOptions,
                product.interestCompoundingPeriodTypeOptions, product.interestPostingPeriodTypeOptions,
                product.interestCalculationTypeOptions, product.interestCalculationDaysInYearTypeOptions,
                product.lockinPeriodFrequencyTypeOptions, product.withdrawalFeeTypeOptions, product.paymentTypeOptions,
                product.accountingRuleOptions, product.accountingMappingOptions, product.charges, product.chargeOptions,
                product.penaltyOptions, product.feeToIncomeAccountMappings, product.penaltyToIncomeAccountMappings, interestRateCharts,
                product.chartTemplate, product.preClosurePenalApplicable, product.preClosurePenalInterest,
                product.preClosurePenalInterestOnType, product.preClosurePenalInterestOnTypeOptions, product.minDepositTerm,
                product.maxDepositTerm, product.minDepositTermType, product.maxDepositTermType, product.inMultiplesOfDepositTerm,
                product.inMultiplesOfDepositTermType, product.isMandatoryDeposit, product.allowWithdrawal,
                product.adjustAdvanceTowardsFuturePayments, product.periodFrequencyTypeOptions, product.minDepositAmount,
                product.depositAmount, product.maxDepositAmount, product.withHoldTax, product.taxGroup, product.taxGroupOptions,
                product.allowFreeWithdrawal, product.addPenaltyOnMissedTargetSavings, product.getProductCategories(),
                product.getProductTypes(), product.getProductCategoryId(), product.getProductTypeId());

    }

    private RecurringDepositProductData(final Long id, final String name, final String shortName, final String description,
            final CurrencyData currency, final BigDecimal nominalAnnualInterestRate, final EnumOptionData interestCompoundingPeriodType,
            final EnumOptionData interestPostingPeriodType, final EnumOptionData interestCalculationType,
            final EnumOptionData interestCalculationDaysInYearType, final Integer lockinPeriodFrequency,
            final EnumOptionData lockinPeriodFrequencyType, final BigDecimal minBalanceForInterestCalculation,
            final EnumOptionData accountingType, final Map<String, Object> accountingMappings,
            final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings,
            final Collection<CurrencyData> currencyOptions, final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions,
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions,
            final Collection<EnumOptionData> interestCalculationTypeOptions,
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions,
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions, final Collection<EnumOptionData> withdrawalFeeTypeOptions,
            final Collection<PaymentTypeData> paymentTypeOptions, final Collection<EnumOptionData> accountingRuleOptions,
            final Map<String, List<GLAccountData>> accountingMappingOptions, final Collection<ChargeData> charges,
            final Collection<ChargeData> chargeOptions, final Collection<ChargeData> penaltyOptions,
            final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings,
            final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings,
            final Collection<InterestRateChartData> interestRateCharts, final InterestRateChartData chartTemplate,
            final boolean preClosurePenalApplicable, final BigDecimal preClosurePenalInterest,
            final EnumOptionData preClosurePenalInterestOnType, final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions,
            final Integer minDepositTerm, final Integer maxDepositTerm, final EnumOptionData minDepositTermType,
            final EnumOptionData maxDepositTermType, final Integer inMultiplesOfDepositTerm,
            final EnumOptionData inMultiplesOfDepositTermType, final boolean isMandatoryDeposit, boolean allowWithdrawal,
            boolean adjustAdvanceTowardsFuturePayments, final Collection<EnumOptionData> periodFrequencyTypeOptions,
            final BigDecimal minDepositAmount, final BigDecimal depositAmount, final BigDecimal maxDepositAmount, final boolean withHoldTax,
            final TaxGroupData taxGroup, final Collection<TaxGroupData> taxGroupOptions, boolean allowFreeWithdrawal,
            final boolean addPenaltyOnMissedTargetSavings, final List<CodeValueData> productCategories,
            final List<CodeValueData> productTypes, final Long productCategoryId, final Long productTypeId) {

        super(id, name, shortName, description, currency, nominalAnnualInterestRate, interestCompoundingPeriodType,
                interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType, lockinPeriodFrequency,
                lockinPeriodFrequencyType, accountingType, accountingMappings, paymentChannelToFundSourceMappings, currencyOptions,
                interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions, interestCalculationTypeOptions,
                interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions,
                accountingRuleOptions, accountingMappingOptions, charges, chargeOptions, penaltyOptions, feeToIncomeAccountMappings,
                penaltyToIncomeAccountMappings, interestRateCharts, chartTemplate, minBalanceForInterestCalculation, withHoldTax, taxGroup,
                taxGroupOptions, false, false, addPenaltyOnMissedTargetSavings, productCategories, productTypes, productCategoryId,
                productTypeId);

        this.preClosurePenalApplicable = preClosurePenalApplicable;
        this.preClosurePenalInterest = preClosurePenalInterest;
        this.preClosurePenalInterestOnType = preClosurePenalInterestOnType;
        this.minDepositTerm = minDepositTerm;
        this.maxDepositTerm = maxDepositTerm;
        this.minDepositTermType = minDepositTermType;
        this.maxDepositTermType = maxDepositTermType;
        this.inMultiplesOfDepositTerm = inMultiplesOfDepositTerm;
        this.inMultiplesOfDepositTermType = inMultiplesOfDepositTermType;
        this.minDepositAmount = minDepositAmount;
        this.depositAmount = depositAmount;
        this.maxDepositAmount = maxDepositAmount;
        this.isMandatoryDeposit = isMandatoryDeposit;
        this.allowWithdrawal = allowWithdrawal;
        this.allowFreeWithdrawal = allowFreeWithdrawal;
        this.adjustAdvanceTowardsFuturePayments = adjustAdvanceTowardsFuturePayments;

        // template data
        this.preClosurePenalInterestOnTypeOptions = preClosurePenalInterestOnTypeOptions;
        this.periodFrequencyTypeOptions = periodFrequencyTypeOptions;
    }

    public EnumOptionData getMinDepositTermType() {
        return minDepositTermType;
    }

    public boolean isPreClosurePenalApplicable() {
        return preClosurePenalApplicable;
    }

    public BigDecimal getPreClosurePenalInterest() {
        return preClosurePenalInterest;
    }

    public EnumOptionData getPreClosurePenalInterestOnType() {
        return preClosurePenalInterestOnType;
    }

    public Integer getMinDepositTerm() {
        return minDepositTerm;
    }

    public Integer getMaxDepositTerm() {
        return maxDepositTerm;
    }

    public EnumOptionData getMaxDepositTermType() {
        return maxDepositTermType;
    }

    public BigDecimal getMinDepositAmount() {
        return minDepositAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public BigDecimal getMaxDepositAmount() {
        return maxDepositAmount;
    }

    public Integer getInMultiplesOfDepositTerm() {
        return inMultiplesOfDepositTerm;
    }

    public EnumOptionData getInMultiplesOfDepositTermType() {
        return inMultiplesOfDepositTermType;
    }

    public boolean isMandatoryDeposit() {
        return isMandatoryDeposit;
    }

    public boolean isAllowWithdrawal() {
        return allowWithdrawal;
    }

    public boolean isAdjustAdvanceTowardsFuturePayments() {
        return adjustAdvanceTowardsFuturePayments;
    }
}
