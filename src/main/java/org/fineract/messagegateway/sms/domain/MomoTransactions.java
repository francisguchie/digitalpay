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
package org.fineract.messagegateway.sms.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="m_momo_transactions")
@Component
public class MomoTransactions extends AbstractPersistableCustom<Long> {

	@Column(name = "uuid", nullable = false)
	private String uuid;
	
	@Column(name = "transaction_type", nullable = false)
	private String transactionType;
	
	@Column(name = "transaction_date", nullable = false)
	private Date transactionDate;
	
	@Column(name = "transaction_amount", nullable = false)
	private BigDecimal transactionAmount;
	
	public MomoTransactions(final String uuid, final String transactionType, final Date transactionDate,
			final BigDecimal transactionAmount) {
		this.uuid = uuid;
		this.transactionType = transactionType;
		this.transactionDate = transactionDate;
		this.transactionAmount = transactionAmount;
	}
	
	public MomoTransactions() {
	}
	
	public String getUUID() {
		return this.uuid;
	}
}
