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
package org.fineract.messagegateway.sms.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface MomoBridgeConstants {
	
	String remoteUsername_paramname = "remoteUsername";

	String remotePassword_paramname = "remotePassword";
	
	String msisdn_paramname = "msisdn";
	
	String amount_paramname = "amount";
	
	String currency_paramname = "currency";
	
	String payerNote_paramname = "payerNote";
	
	String payeeNote_paramname = "payeeNote";
	
	String externalId_paramname = "externalId";

	Set<String> supportedParameters = new HashSet<>(Arrays.asList(remoteUsername_paramname,
			remotePassword_paramname, msisdn_paramname, amount_paramname, currency_paramname, payerNote_paramname, payeeNote_paramname, externalId_paramname));
}
