--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

-- liquibase formatted sql
-- changeset fineract:1
-- MySQL dump 10.13  Distrib 5.1.60, for Win32 (ia32)
--
-- Host: localhost    Database: fineract_default
-- ------------------------------------------------------
-- Server version	5.1.60-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES UTF8MB4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

UPDATE stretchy_report SET report_sql = " WITH RankedAddresses AS (SELECT client_id,
                                                                         address_id,
                                                                         ROW_NUMBER() OVER (PARTITION BY client_id ORDER BY address_id DESC) AS row_num
                                                                  FROM m_client_address)
                                         SELECT l.id                                                                                               AS loanId,
                                                l.account_no                                                                                       AS accountNumber,
                                                l.loan_status_id                                                                                   AS loanStatus,
                                                l.currency_code                                                                                    AS currencyType,
                                                country_cv.code_value                                                                              AS country,
                                                mc.firstname                                                                                       AS surName,
                                                DATEDIFF(NOW(), mlaa.overdue_since_date_derived)                                                   AS daysInArrears,
                                                mc.firstname                                                                                       AS foreName1,
                                                mc.middlename                                                                                      AS foreName2,
                                                mc.lastname                                                                                        AS foreName3,
                                                l.principal_disbursed_derived                                                                      AS openingBalance,
                                                CASE
                                                    WHEN l.repayment_period_frequency_enum = 0 THEN 'DLY'
                                                    WHEN l.repayment_period_frequency_enum = 1 THEN 'WKY'
                                                    WHEN l.repayment_period_frequency_enum = 2 THEN 'MTH'
                                                    WHEN l.repayment_period_frequency_enum = 3 THEN 'ANN'
                                                    ELSE 'IRR' END                                                                                 AS accountRepaymentTerm,
                                                l.total_outstanding_derived                                                                        AS currentBalance,
                                                 IF(l.loan_type_enum = 1, 'O', 'G')                                                                AS accountOwner,
                                                CASE
                                                    WHEN l.repayment_period_frequency_enum = 1 THEN 'W'
                                                    WHEN l.repayment_period_frequency_enum = 2 THEN 'M'
                                                    WHEN l.repayment_period_frequency_enum = 3
                                                        THEN 'A' END                                                                               AS incomeFrequency,
                                                l.principal_disbursed_derived + l.interest_charged_derived                                         AS scheduledPaymentAmount,
                                                mc.mobile_no                                                                                       AS mobileTelephone,
                                                (l.principal_repaid_derived + l.interest_repaid_derived + l.fee_charges_repaid_derived +
                                                 l.penalty_charges_repaid_derived)                                                                 AS actualPaymentAmount,
                                                l.disbursedon_date                                                                                 AS dateAccountOpened,
                                                l.nominal_interest_rate_per_period                                                                 AS interestRateAtDisbursement,
                                                info.number_of_dependents                                                                          AS noOfDependants,
                                                nationality_cv.code_value                                                                          AS nationality,
                                                UPPER(title_cv.code_value)                                                                         AS salutation,
                                                ra.postal_code                                                                                     AS postalCode,
                                                province_cv.code_value                                                                             AS physicalAddressProvince,
                                                CASE
                                                    WHEN marital_cv.code_value = 'Single' THEN 'S'
                                                    WHEN marital_cv.code_value = 'Married' THEN 'M'
                                                    WHEN marital_cv.code_value = 'Divorced' THEN 'D'
                                                    WHEN marital_cv.code_value = 'Widowed' THEN 'W'
                                                    ELSE 'O' END                                                                                   AS maritalStatus,
                                                ra.postal_code                                                                                     AS postalAddressNumber,
                                                l.approvedon_date                                                                                  AS approvalDate,
                                                CASE WHEN gender_cv.code_value = 'Male' THEN 'M' WHEN gender_cv.code_value = 'Female' THEN 'F' END AS gender,
                                                first_payment.firstPaymentDate                                                                     AS firstPaymentDate,
                                                l.closedon_date                                                                                    AS dateClosed,
                                                CASE
                                                    WHEN l.loan_status_id = 300 THEN 'A'
                                                    WHEN l.loan_status_id = 600 THEN 'C'
                                                    WHEN l.loan_status_id = 601 THEN 'W'
                                                    WHEN l.loan_status_id = 700
                                                        THEN 'X' END                                                                               AS accountStatus,
                                                l.number_of_repayments                                                                             AS termsDuration,
                                                l.last_repayment_date                                                                              AS lastPaymentDate,
                                                mc.date_of_birth                                                                                   AS dateOfBirth,
                                                l.maturedon_date                                                                                   AS finalPaymentDate,
                                                mlaa.principal_overdue_derived                                                                     AS amountPastDue,
                                                40                                                                                                 AS category,
                                                'Other personal service activities n.e.c.'                                                         AS sectorOfActivity,
                                                'I'                                                                                                AS accountType,
                                                ra.physical_address_district                                                                       AS physicalAddressDistrict,
                                                ''                                                                                                 AS groupName,
                                                CASE
                                                    WHEN DATEDIFF(NOW(), mlaa.overdue_since_date_derived) <= 90   THEN 'C'
                                                    WHEN l.loan_status_id IN(600,601,700) THEN 'C'
                                                    ELSE 'D'
                                                    END                                                                                                AS currentBalanceIndicator,
                                                ra.physical_address_sector                                                                         AS physicalAddressSector,
                                                0                                                                                                  AS numberOfJointLoanParticipants,
                                                ra.physical_address_cell                                                                           AS physicalAddressCell,
                                                13                                                                                                 AS nature,
                                                other_info.national_identification_number                                                          AS nationalId,
                                                other_info.passport_number                                                                         AS passportNumber,
                                                CASE
                                                    WHEN mlaa.overdue_since_date_derived IS NULL OR DATEDIFF(NOW(), mlaa.overdue_since_date_derived) < 30 THEN 1
                                                    WHEN DATEDIFF(NOW(), mlaa.overdue_since_date_derived) BETWEEN 31 AND 90 THEN 2
                                                    WHEN DATEDIFF(NOW(), mlaa.overdue_since_date_derived) BETWEEN 91 AND 180 THEN 3
                                                    WHEN DATEDIFF(NOW(), mlaa.overdue_since_date_derived) BETWEEN 181 AND 365 THEN 4
                                                    WHEN DATEDIFF(NOW(), mlaa.overdue_since_date_derived) BETWEEN 366 AND 719 THEN 5
                                                    WHEN DATEDIFF(NOW(), mlaa.overdue_since_date_derived) > 720
                                                        THEN 6 END                                                                                 AS classification,
                                                ''                                                                                                 AS emailAddress,
                                                'T'                                                                                                AS residenceType,
                                                0                                                                                                  AS availableCredit,
                                                0                                                                                                  AS income,
                                                ''                                                                                                 AS homeTelephone,
                                                ''                                                                                                 AS workTelephone,
                                                l.last_modified_on_utc                                                                             AS dateAccountUpdated,
                                                r.installments_in_arrears                                                                          AS installmentsInArrears
                                         FROM m_loan l
                                                  INNER JOIN m_client mc ON l.client_id = mc.id
                                                  LEFT JOIN m_client_recruitment_survey mcrs ON mc.id = mcrs.client_id
                                                  LEFT JOIN m_code_value country_cv ON mcrs.country_cv_id = country_cv.id
                                                  LEFT JOIN m_loan_arrears_aging mlaa ON l.id = mlaa.loan_id
                                                  LEFT JOIN m_client_other_info info ON mc.id = info.client_id
                                                  LEFT JOIN m_code_value nationality_cv ON info.nationality_cv_id = nationality_cv.id
                                                  LEFT JOIN m_client_additional_info ad_info ON mc.id = ad_info.client_id
                                                  LEFT JOIN m_client_other_info other_info ON mc.id = other_info.client_id
                                                  LEFT JOIN m_code_value marital_cv ON ad_info.marital_status = marital_cv.id
                                                  LEFT JOIN m_code_value gender_cv ON mc.gender_cv_id = gender_cv.id
                                                  LEFT JOIN m_code_value title_cv ON ad_info.title = title_cv.id
                                                  LEFT JOIN (SELECT loan_id, transaction_date AS firstPaymentDate
                                                             FROM (SELECT loan_id,
                                                                          transaction_date,
                                                                          ROW_NUMBER() OVER (PARTITION BY loan_id ORDER BY transaction_date) AS row_num
                                                                   FROM m_loan_transaction
                                                                   WHERE transaction_type_enum = 2) ranked_transactions
                                                             WHERE row_num = 1) AS first_payment ON l.id = first_payment.loan_id
                                                  LEFT JOIN (SELECT client_id, MAX(address_id) AS last_address_id
                                                             FROM m_client_address
                                                             GROUP BY client_id) AS last_client_address ON mc.id = last_client_address.client_id
                                                  LEFT JOIN m_address ra ON last_client_address.last_address_id = ra.id
                                                  LEFT JOIN (SELECT loan_id, COUNT(*) AS installments_in_arrears
                                                             FROM m_loan_repayment_schedule
                                                             WHERE duedate <= CURRENT_DATE
                                                               AND completed_derived = FALSE
                                                               AND obligations_met_on_date IS NULL
                                                             GROUP BY loan_id) AS r ON l.id = r.loan_id
                                                  LEFT JOIN m_code_value province_cv ON ra.state_province_id = province_cv.id
                                         WHERE l.loan_status_id IN (300, 600, 601, 700)
                                           AND l.currency_code = 'RWF'
                                           AND first_payment.firstPaymentDate IS NOT NULL
                                           AND l.last_repayment_date IS NOT NULL
                                           AND mc.legal_form_enum = 1
                                           AND (l.stop_consumer_credit_upload_to_trans_union IS NULL OR l.stop_consumer_credit_upload_to_trans_union = false)
                                         order by l.id " WHERE id = (SELECT tbl.id FROM (SELECT sr.id FROM stretchy_report sr WHERE sr.report_name = "Rwanda Consumer Credit To TransUnion (CRB) Report") AS tbl);
