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
package org.fineract.messagegateway.sms.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.helpers.ApiParameterError;
import org.fineract.messagegateway.helpers.DataValidatorBuilder;
import org.fineract.messagegateway.helpers.FromJsonHelper;
import org.fineract.messagegateway.service.SecurityService;
import okhttp3.OkHttpClient;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.Response;
import okhttp3.MediaType;

import org.fineract.messagegateway.sms.api.SmsBridgeApiResource;
import org.fineract.messagegateway.sms.constants.MomoBridgeConstants;
import org.fineract.messagegateway.sms.constants.SmsConstants;
import org.fineract.messagegateway.sms.data.MomoTransactionData;
import org.fineract.messagegateway.sms.data.MomoWithdrawData;
import org.fineract.messagegateway.sms.domain.MomoBridge;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.repository.MomoConfigurationRepository;
import org.fineract.messagegateway.sms.repository.MomoTransactionsRepository;
import org.fineract.messagegateway.sms.repository.SMSBridgeRepository;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.math.BigDecimal;
import org.fineract.messagegateway.sms.domain.MomoTransactions;

import java.util.Map;
import org.fineract.messagegateway.sms.serialization.SmsBridgeSerializer;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Credentials;

@Service(value = "apacheServiceBridge")
public class MomoBridgeService {
	private static final Logger LOG = LoggerFactory.getLogger(MomoBridgeService.class);
	private final MomoConfigurationRepository momoConfigurationRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final MomoTransactionsRepository momoTransactionsRepository;

	@Autowired
	public MomoBridgeService(final MomoConfigurationRepository momoConfigurationRepository, final FromJsonHelper fromApiJsonHelper,
			final MomoTransactionsRepository momoTransactionsRepository) {
		this.momoConfigurationRepository = momoConfigurationRepository;
		this.momoTransactionsRepository = momoTransactionsRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
	}



	public Response okHttpMethod(String url, String body, String type, String referenceId) {
		 System.out.print("URL**: "+url +"--");
		 String responseMessage = null;
		 OkHttpClient client = new OkHttpClient();
         HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
         String urlokhttp = urlBuilder.build().toString();
         String credential = null;
         Request request = null;

         MomoBridge subscriptionKeyConfig = this.momoConfigurationRepository.findOneByName("subscription_key");
		 String subscriptionKey = subscriptionKeyConfig.getValue();

		 MomoBridge mifosUsernameConfig = this.momoConfigurationRepository.findOneByName("mifos_username");
		 String username = mifosUsernameConfig.getValue();

		 MomoBridge mifosPasswordConfig = this.momoConfigurationRepository.findOneByName("mifos_password");
		 String password = mifosPasswordConfig.getValue();



         if(type.equals("fineract")) {
         credential = Credentials.basic(username, password);

         final MediaType mediaType = MediaType.parse("application/json");
         RequestBody requestBody = RequestBody.create(body, mediaType);
        	  request = new Request.Builder().header("Content-Type", "application/json")
            		 .header("Authorization", credential).url(urlokhttp).post(requestBody).build();

         }
         else if (type.equals("fineract-get")) {
        	 System.out.print("fineract-get");
        	 credential = Credentials.basic(username, password);

             final MediaType mediaType = MediaType.parse("application/json");
            // RequestBody requestBody = RequestBody.create(body, mediaType);
            	  request = new Request.Builder().header("Content-Type", "application/json")
                		 .header("Authorization", credential).url(urlokhttp).get().build();
         }
         else if(type.equals("getapiuser")) {
         request = new Request.Builder().header("Ocp-Apim-Subscription-Key", subscriptionKey)
        		  .url(urlokhttp).get().build();
         }
         else if(type.equals("postapikey")) {
        	 System.out.println("\n postapikey:");
        	 final MediaType mediaType = MediaType.parse("application/json");
        	 RequestBody requestBody = RequestBody.create(body, mediaType);
             request = new Request.Builder().header("Ocp-Apim-Subscription-Key", subscriptionKey)
            		  .url(urlokhttp).post(requestBody).build();
             }
         else if(type.equals("postapiuser")) {
        	 MomoBridge uuid = this.momoConfigurationRepository.findOneByName("uuid");
    		 String xReferenceId = uuid.getValue(); //create a function just to create new uuid and save it in the database
    		 final MediaType mediaType = MediaType.parse("application/json");
    		 RequestBody requestBody = RequestBody.create(body, mediaType);
             request = new Request.Builder().header("Ocp-Apim-Subscription-Key", subscriptionKey)
            		 .header("X-Reference-Id", xReferenceId)
            		  .url(urlokhttp).post(requestBody).build();
             }
         else if(type.equals("momo")){
        	 MomoBridge token = this.momoConfigurationRepository.findOneByName("token");
        	 MomoBridge uuid = this.momoConfigurationRepository.findOneByName("uuid"); //create new uuid everytime
    		// String xReferenceId = uuid.getValue();



    		 final MediaType mediaType = MediaType.parse("application/json");
    		 RequestBody requestBody = RequestBody.create(body, mediaType);





             credential = "Bearer " + token.getValue();
                 request = new Request.Builder().header("Ocp-Apim-Subscription-Key", subscriptionKey)
                		 .header("Authorization", credential)
                		 .header("X-Reference-Id", referenceId)
                		 .header("X-Target-Environment", "sandbox").url(urlokhttp).post(requestBody).build();
         }
         else if(type.equals("token")) {
        	 MomoBridge uuid = this.momoConfigurationRepository.findOneByName("uuid");
             username = uuid.getValue();

             MomoBridge apiKey = this.momoConfigurationRepository.findOneByName("api_key");
             password = apiKey.getValue();
             System.out.println("password : "+ password);
        	 credential = Credentials.basic(username, password);
        	 final MediaType mediaType = MediaType.parse("application/json");
        	 RequestBody requestBody = RequestBody.create(body, mediaType);

        	 request = new Request.Builder()
            		 .header("Authorization", credential)
            		 .header("Ocp-Apim-Subscription-Key", subscriptionKey).url(urlokhttp).post(requestBody).build();
         }
         else if(type.equals("uuid")) {
        	 request = new Request.Builder().url(urlokhttp).get().build();
         }


        // response = null;
         Response response = null;
         Integer responseCode = 0;
         try {
         response = client.newCall(request).execute();
          //   responseCode = response.code();
          // responseMessage = response.body().string();
          // System.out.println("responseMessage :" + responseMessage);

         } catch (IOException e) {
                  LOG.error("error occured in HTTP request-response method.", e);

              }
         return response;
	}


	public String generateUUID() {
		String responseMessage = null;
		MomoBridge uuidurl = this.momoConfigurationRepository.findOneByName("uuid_url");
		OkHttpClient client = new OkHttpClient();
		HttpUrl.Builder urlBuilder = HttpUrl.parse(uuidurl.getValue()).newBuilder();
        String urlokhttp = urlBuilder.build().toString();
        String credential = null;
        Request request = null;

        request = new Request.Builder().url(urlokhttp).get().build();
        Response response = null;
        Integer responseCode = 0;
        try {
            response = client.newCall(request).execute();
            responseMessage = response.body().string();
            System.out.println("uuid response: " + response);
        } catch (IOException e) {
            LOG.error("error occured in HTTP request-response method.", e);
        }
        return responseMessage;
	}

	public MomoTransactionData validateCreate(final String json) {
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, MomoBridgeConstants.supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("smsBridge");
		final JsonElement element = this.fromApiJsonHelper.parse(json);

		final String msisdn = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.msisdn_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.msisdn_paramname).value(msisdn).notBlank();

		final String amount = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.amount_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.amount_paramname).value(amount).notBlank();

		final String currency = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.currency_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.currency_paramname).value(currency).notBlank();

		final String payerNote = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.payerNote_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.payerNote_paramname).value(payerNote).notBlank();

		final String payeeNote = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.payeeNote_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.payeeNote_paramname).value(payeeNote).notBlank();

		final String externalId = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.externalId_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.externalId_paramname).value(externalId).notBlank();

		MomoTransactionData momoBridge = new MomoTransactionData(msisdn, amount, currency, payerNote, payeeNote, externalId) ;

		return momoBridge;
	}
	
	public MomoWithdrawData validateWithdrawCreate(final String json) {
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, MomoBridgeConstants.withdrawSupportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("smsBridge");
		final JsonElement element = this.fromApiJsonHelper.parse(json);

		final String transactionAmount = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.transactionAmount_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.transactionAmount_paramname).value(transactionAmount).notBlank();

		final String transactionDate = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.transactionDate_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.transactionDate_paramname).value(transactionDate).notBlank();

		final String paymentTypeId = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.paymentTypeId_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.paymentTypeId_paramname).value(paymentTypeId).notBlank();

		final String note = this.fromApiJsonHelper.extractStringNamed(MomoBridgeConstants.note_paramname, element);
		baseDataValidator.reset().parameter(MomoBridgeConstants.note_paramname).value(note).notBlank();

		MomoWithdrawData momoWithdraw = new MomoWithdrawData(transactionAmount, transactionDate, paymentTypeId, note) ;

		return momoWithdraw;
	}
	
	
	public String getMomoResponse(final String jsonBody) {
		String apiKey = null;
    	 String url = null;
    	 String uuid = null;
    	 MomoBridge uuidConfig = this.momoConfigurationRepository.findOneByName("uuid");
    	 System.out.println("uuidConfig:"+ uuidConfig);
    	 if(uuidConfig.getValue().equals("null")) { //saved UUID
    		 System.out.println("uuidConfig null");
    		 MomoBridge uuidurl = this.momoConfigurationRepository.findOneByName("uuid_url");
    		 url = uuidurl.getValue();
    		 final Response apiUser = okHttpMethod(url, null, "uuid", null);
    		
    		 try {
    		 uuid = apiUser.body().string();
    		 System.out.println(" 200 apiuser** "+uuid);
    		 }catch (IOException e) {
                 LOG.error("error occured in HTTP request-response method.", e);
             }
    		 
    		 uuidConfig.setValue(uuid);
    		 this.momoConfigurationRepository.save(uuidConfig);
    	 }
    	 
    	 MomoBridge apiKeyConfig = this.momoConfigurationRepository.findOneByName("api_key");
    	 if(apiKeyConfig.getValue().equals("null")) { //saved apikey
    		 
    		 MomoBridge apiUserUrl = this.momoConfigurationRepository.findOneByName("get_api_user_url");
    		 url = apiUserUrl.getValue();
    		 uuidConfig = this.momoConfigurationRepository.findOneByName("uuid");
    		 String xReferenceId = uuidConfig.getValue();
    		 String urlValue = url.replace("{uuid}", xReferenceId);
    		 Response apiUser = okHttpMethod(urlValue, null, "getapiuser", null); //done 
    		 
    		 Integer code = apiUser.code();
    		 System.out.println("code: "+ code);
    		 if(code.equals(200)) { //apiuser is ok, create apikey
    			 System.out.println("200: ");
    			 MomoBridge apiKeyUrl = this.momoConfigurationRepository.findOneByName("api_key_url");
    			 url = apiKeyUrl.getValue();
    			 urlValue = url.replace("{uuid}", xReferenceId);
    			 apiUser = okHttpMethod(urlValue, "", "postapikey", null); //done
    			 
    			 String apiKeyValue = null;
    			 try {
    			 apiKeyValue = apiUser.body().string();
    			 System.out.println(" 200 apiuser** "+apiKeyValue);
    			 }catch (IOException e) {
                     LOG.error("error occured in HTTP request-response method.", e);
                 }
    			 JsonParser parser = new JsonParser();
    			 JsonObject reportObject = parser.parse(apiKeyValue).getAsJsonObject();
    		     apiKey = reportObject.get("apiKey").getAsString();
    		     
    		     apiKeyConfig = this.momoConfigurationRepository.findOneByName("api_key");
    		     apiKeyConfig.setValue(apiKey);
    		     this.momoConfigurationRepository.save(apiKeyConfig);
    		 }
    		 else {
    			 System.out.println("else apikey: ");// apiuser is not ok, post apiuser with existing uuid and validate 
    			 apiUserUrl = this.momoConfigurationRepository.findOneByName("api_user_url");
        		 url = apiUserUrl.getValue();
        		// String webHook = this.momoConfigurationRepository.findOneByName("web_hook").getValue();
        		// String body = "{'':webHook }";
        		 apiUser = okHttpMethod(url, "", "postapiuser", null); //done (NOT WORKING)
        		 
        		 code = apiUser.code();
        		 System.out.println("else code : " + code);
        		 //call this function again
        		 
    		 }
    	 }
    	 MomoBridge token = this.momoConfigurationRepository.findOneByName("token"); 
    	 if(token.getValue().equals("null")){ //also check the expiry of the token
    		 MomoBridge tokenUrl = this.momoConfigurationRepository.findOneByName("create_token_url");
    		 Response tokenResponse = okHttpMethod(tokenUrl.getValue(), "", "token", null);
    		 
    		 String tokenValue = null;
    		 try {
    		 tokenValue = tokenResponse.body().string();
    		 System.out.println(" tokenResponse response** "+tokenValue);
    		 }catch (IOException e) {
                 LOG.error("error occured in HTTP request-response method.", e);
             }
    		
    		 JsonParser parser = new JsonParser();
    		 JsonObject reportObject = parser.parse(tokenValue).getAsJsonObject();
		     String accessToken = reportObject.get("access_token").getAsString();
		    
    		 token = this.momoConfigurationRepository.findOneByName("token");
    		
    		 token.setValue(accessToken);
    		
    		 this.momoConfigurationRepository.save(token);
    	 }
    	 
    	 
    	 final MomoBridge tranferUrlConfig = this.momoConfigurationRepository.findOneByName("transfer_url");
         final String transferUrl = tranferUrlConfig.getValue();
         
         MomoTransactionData momoBridge = validateCreate(jsonBody);
         String transaction = "{'amount': "+momoBridge.getAmount()+",'currency':"+momoBridge.getCurrency()+",'externalId': "+momoBridge.getExternalId()+",'payee': {'partyIdType': 'MSISDN','partyId': '"+momoBridge.getMsisdn()+"'},'payerMessage': '"+momoBridge.getPayerNote()+"','payeeNote': '"+momoBridge.getPayeeNote()+"'}";
         String xReferenceId =  generateUUID();
         Response transferResponse = okHttpMethod(transferUrl, transaction, "momo", xReferenceId);
        
       //getting param from body to save in m_momo_transactions table
         Integer statusCode = 202;
         Date date = new Date();
         BigDecimal transactionAmount= new BigDecimal(momoBridge.getAmount());
         if(transferResponse.code()==statusCode) {
        	 MomoTransactions momoTransactions = new MomoTransactions(xReferenceId, "Disbursement", date, transactionAmount);
        	 this.momoTransactionsRepository.save(momoTransactions);
         }
         
         
         String response = null;
         try {
        	 response =  transferResponse.body().string();
        	 
         }catch (IOException e) {
             LOG.error("error occured in HTTP request-response method.", e);
         }
         System.out.println("responseCode** "+transferResponse.code());
         return response;
	}
}
