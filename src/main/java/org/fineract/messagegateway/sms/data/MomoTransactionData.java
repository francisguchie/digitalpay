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
package org.fineract.messagegateway.sms.data;

public class MomoTransactionData {
	
	private String msisdn;
	private String amount;
	private String currency;
	private String payerNote;
	private String payeeNote;
	private String externalId;

	public MomoTransactionData(final String msisdn, final String amount, final String currency, final String payerNote, final String payeeNote,
			final String externalId) {
		this.msisdn = msisdn;
		this.amount = amount;
		this.currency = currency;
		this.payerNote = payerNote;
		this.payeeNote = payeeNote;
		this.externalId = externalId;
	}
	
	public String getMsisdn() {
		return this.msisdn;
	}
	
	public String getAmount() {
		return this.amount;
	}
	
	public String getCurrency() {
		return this.currency;
	}
	
	public String getPayerNote() {
		return this.payerNote;
	}
	
	public String getPayeeNote() {
		return this.payeeNote;
	}
	
	public String getExternalId() {
		return this.externalId;
	}
}