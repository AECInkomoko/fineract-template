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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Data
@Entity
@Table(name = "m_failed_loan_creation_on_data_migration")
public class FailedLoanCreationOnDataMigration extends AbstractPersistableCustom {

    @Column(name = "client_Id")
    private Long client;

    @Column(name = "odoo_loan_number")
    private String odooLoanNumber;

    @Column(name = "odoo_loan_id")
    private String odooLoanId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "error_msg")
    private String errorMsg;

    @Column(name = "json_object")
    private String jsonObject;
}
