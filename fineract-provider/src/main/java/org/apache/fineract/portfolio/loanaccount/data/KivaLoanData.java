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
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class KivaLoanData {

    private int borrower_count;
    private String internal_loan_id;
    private String internal_client_id;
    private String partner_id;
    private String partner;
    private String kiva_id;
    private String uuid;
    private String name;
    private String location;
    private String status;
    private String loan_price;
    private String loan_local_price;
    private String loan_currency;
    private long create_time;
    private Long ended_time;
    private Long refunded_time;
    private Long expired_time;
    private long defaulted_time;
    private long planned_expiration_time;
    private long planned_inactive_expire_time;
    private boolean delinquent;
    private Long issue_feedback_time;
    private Long issue_reported_by;
    private String flexible_fundraising_enabled;
    private BigDecimal fundedAmount;
}
