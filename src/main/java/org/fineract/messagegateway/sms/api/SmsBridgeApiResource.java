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
package org.fineract.messagegateway.sms.api;

import java.util.Collection;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;

import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.exception.UnsupportedParameterException;
import org.fineract.messagegateway.helpers.ApiGlobalErrorResponse;
import org.fineract.messagegateway.helpers.PlatformApiDataValidationExceptionMapper;
import org.fineract.messagegateway.helpers.PlatformResourceNotFoundExceptionMapper;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.OkHttpClient;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import org.fineract.messagegateway.sms.domain.ParserUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fineract.messagegateway.helpers.UnsupportedParameterExceptionMapper;
import org.fineract.messagegateway.sms.data.MomoTransactionData;
import org.fineract.messagegateway.sms.domain.MomoBridge;
import org.fineract.messagegateway.sms.domain.MomoTransactions;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.fineract.messagegateway.sms.repository.MomoConfigurationRepository;
import org.fineract.messagegateway.sms.repository.MomoTransactionsRepository;
import org.fineract.messagegateway.sms.service.MomoBridgeService;
import org.fineract.messagegateway.sms.service.SMSBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.nodes.Node;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//

//
@RestController
public class SmsBridgeApiResource {

	private final SMSBridgeService smsBridgeService;
	private final MomoBridgeService momoBridgeService;
	private final MomoConfigurationRepository momoConfigurationRepository;
	private final MomoTransactionsRepository momoTransactionsRepository;
      
private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name", "systemDefined"));
private static final Logger LOG = LoggerFactory.getLogger(SmsBridgeApiResource.class);
	
	@Autowired
    public SmsBridgeApiResource(final SMSBridgeService smsBridgeService, final MomoBridgeService momoBridgeService, final MomoConfigurationRepository momoConfigurationRepository,
    		final MomoTransactionsRepository momoTransactionsRepository) {
		this.smsBridgeService = smsBridgeService ;
		this.momoBridgeService = momoBridgeService;
		this.momoConfigurationRepository = momoConfigurationRepository;
		this.momoTransactionsRepository = momoTransactionsRepository;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long> createSMSBridgeConfig(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey,
    		@RequestBody final String smsBridgeJson) {
    		 Long bridgeId = this.smsBridgeService.createSmsBridgeConfig(tenantId, appKey, smsBridgeJson) ;
    		 return new ResponseEntity<>(bridgeId, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{bridgeId}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long>updateSMSBridgeConfig(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String tenantAppKey,
    		@PathVariable("bridgeId") final Long bridgeId, @RequestBody final String smsBridge) {
    	this.smsBridgeService.updateSmsBridge(tenantId, tenantAppKey, bridgeId, smsBridge);
        return new ResponseEntity<>(bridgeId, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{bridgeId}", method = RequestMethod.DELETE, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long>deleteSMSBridgeConfig(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String tenantAppKey,
    		@PathVariable("bridgeId") final Long bridgeId) {
		this.smsBridgeService.deleteSmsBridge(tenantId, tenantAppKey, bridgeId);
        return new ResponseEntity<>(bridgeId, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Collection<SMSBridge>> getAllSMSBridgeConfigs(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey) {
        Collection<SMSBridge> bridges = this.smsBridgeService.retrieveProviderDetails(tenantId, appKey) ;
        return new ResponseEntity<>(bridges, HttpStatus.OK);
    }

    @RequestMapping(value = "/{bridgeId}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<SMSBridge> getSMSBridgeConfig(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey,
    		@PathVariable("bridgeId") final Long bridgeId) {
        SMSBridge bridge = this.smsBridgeService.retrieveSmsBridge(tenantId, appKey, bridgeId);
		return new ResponseEntity<>(bridge, HttpStatus.OK);
    }

     @RequestMapping(value = "/loanAccounts", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public String getLoanAccounts(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader("accountNumber") final String accountNumber,
    		@RequestHeader("amount") final String amount) {
       // final Collection<CodeData> codes = this.readPlatformService.retrieveAllCodes();

    	
         RequestBody requestBody = null;
         String responseMessage = null;
         String url = "https://release160.guchietech.pw/fineract-provider/api/v1/clients?sqlSearch=c.account_No="+accountNumber+"&tenantIdentifier=default";
       
         final Response response = this.momoBridgeService.okHttpMethod(url, null, "fineract", null);
         try {
         responseMessage = response.body().string();
         }catch (IOException e) {
             LOG.error("error occured in HTTP request-response method.", e);
         }
     
        try {
			//	JsonObject reportObject =  JsonParser.parseString(responseMessage).getAsJsonObject();
        	JsonParser parser = new JsonParser();
        	JsonElement jsonElement = parser.parse(responseMessage);
        	
        	JsonObject childObject = jsonElement.getAsJsonObject();
        	
        	JsonElement pageitemsElement= childObject.get("pageItems");
        	
        	JsonArray loans = (JsonArray) pageitemsElement;
        	
        	
        	for ( int i=0; i<loans.size(); ++i) {
        		JsonObject data = loans.get(i).getAsJsonObject();
        		JsonObject statusObject = data.get("status").getAsJsonObject();
        		
        		String statusId = statusObject.get("id").getAsString();  //clientId
        		System.out.println("id : " + statusId);
        		
                if(statusId.equals("300")) {
                	
                	String clientId = data.get("id").getAsString(); //clientId
                	System.out.println("clientId : " + clientId);
                	String clientUrl = "https://release160.guchietech.pw/fineract-provider/api/v1/clients/"+clientId+"?tenantIdentifier=default";
                	Response clientResponseMessage = this.momoBridgeService.okHttpMethod(clientUrl, null, "fineract", null);
                	try {
                	responseMessage = clientResponseMessage.body().string();
                	}catch (IOException e) {
                        LOG.error("error occured in HTTP request-response method.", e);
                    }
                	
                	//JsonElement loanElement = responseMessage.parse(responseMessage);
                 	//JsonObject loanObject = loanElement.getAsJsonObject();
                	//JsonParser parser = new JsonParser();
                 	JsonObject reportObject = parser.parse(responseMessage).getAsJsonObject();
        		 
                 	String savingsId= reportObject.get("savingsAccountId").getAsString();
                 	System.out.println("savingsId: " +  savingsId);
       
                 	LocalDateTime myDateObj = LocalDateTime.now();
                    System.out.println("Before formatting: " + myDateObj);
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                    String formattedDate = myDateObj.format(myFormatObj);
                 	String transaction = "{'transactionDate':'"+formattedDate+"','transactionAmount':'"+amount+"','paymentTypeId':1,'locale':'en','dateFormat':'dd MMMM yyyy'}";
                 	String savingsUrl ="https://release160.guchietech.pw/fineract-provider/api/v1/savingsaccounts/"+savingsId+"/transactions?command=deposit&tenantIdentifier=default";
                 	Response SavingsResponseMessage = this.momoBridgeService.okHttpMethod(savingsUrl, transaction, "fineract", null);
                 	try {
                 	responseMessage = SavingsResponseMessage.body().string();
                 	}catch (IOException e) {
                        LOG.error("error occured in HTTP request-response method.", e);
                    }
                }
            }
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return  responseMessage;
    }
     
     
     
     /*********************************************FROM HERE Payment From Encot TO MOMO ************************************/
     @RequestMapping(value = "/payment", method = RequestMethod.POST, consumes = {"application/xml", "text/xml;charset=utf-8"}, produces = {"application/xml", "text/xml;charset=utf-8"})
     public String postDepositMomo(@RequestBody String xml) {
    	 String responseMessage = null;
    	 
    	 Map<String, String> response = ParserUtil.processXML(xml);
    	 
    	 String savingsAccountId = response.get("message"); //defaultSavingsId is passed in Message
    	 System.out.println("savingsId : " + savingsAccountId);
    	 
    	 String msisdn = response.get("accountholderid"); 
    	 System.out.println("msisdn : " + msisdn);
    	 String phoneNumber = ParserUtil.extractPhoneNumber(msisdn);
    	 System.out.println("phoneNumber : " + phoneNumber);
    	 
    	 MomoBridge paymentTypeConfig = this.momoConfigurationRepository.findOneByName("paymentType");
	     String paymentType = paymentTypeConfig.getValue();
	        
    	 
    	 if(!savingsAccountId.isEmpty()) {
    		    System.out.println("savings account NOT blank");
    		    //String depositUrl = "https://livetest.encot.net/fineract-provider/api/v1/savingsaccounts/{savingsAccountId}/transactions?command=deposit&tenantIdentifier=default";
    		    MomoBridge depositUrlConfig = this.momoConfigurationRepository.findOneByName("depositSavingsUrl");
    	        String url = depositUrlConfig.getValue();
    	        String depositUrl = url.replace("{defaultSavingsId}", savingsAccountId);
    		 
    		    LocalDateTime myDateObj = LocalDateTime.now();
    	      
    	        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    	        String formattedDate = myDateObj.format(myFormatObj);
    	        
    	        String amount = response.get("amount");
    	       
    	        String transaction = "{'transactionDate':'"+formattedDate+"','transactionAmount':'"+amount+"','paymentTypeId':"+paymentType+",'locale':'en','dateFormat':'dd MMMM yyyy'}";
    	        
    	        
    	        Response SavingsResponseMessage = this.momoBridgeService.okHttpMethod(depositUrl, transaction, "fineract", null);
    	        
    	        
    	        try {
    	            responseMessage = SavingsResponseMessage.body().string();
    	            }catch (IOException e) {
    	                LOG.error("error occured in HTTP request-response method.", e);
    	            }
    	        System.out.println("deposit successful--- >" + responseMessage);
    	    	 }
    	 else if(!phoneNumber.isEmpty()) {
    		 
    	 System.out.println("savings account blank");
    	 String transactionId = response.get("transactionid");
    	
    	 //fetch the client account number from msisdn
    	// String clientUrl = "https://livetest.encot.net/fineract-provider/api/v1/clients?sqlSearch=c.mobile_no={mobileNumber}&tenantIdentifier=default";
    	 MomoBridge clientSqlSearchUrlConfig = this.momoConfigurationRepository.findOneByName("clientSqlSearchUrl");
	     String clientSearchUrl = clientSqlSearchUrlConfig.getValue();
	    // String url = clientSearchUrl.getValue();
	     clientSearchUrl = clientSearchUrl.replace("{mobileNumber}", phoneNumber);
	     
	     
    	 final Response clientDetails = this.momoBridgeService.okHttpMethod(clientSearchUrl, null, "fineract-get", null);
    	 System.out.println("clientDetails : " + clientDetails);
    	
    	
         try {
         responseMessage = clientDetails.body().string();
         }catch (IOException e) {
             LOG.error("error occured in HTTP request-response method.", e);
         }
     
         
         
    	 //fetch the default savings account from client account no.
    	 
    	JsonParser parser = new JsonParser();
     	JsonElement jsonElement = parser.parse(responseMessage);
     	
     	JsonObject childObject = jsonElement.getAsJsonObject();
     	
     	JsonElement pageitemsElement= childObject.get("pageItems");
     	
     	JsonArray clientDetailsJson = (JsonArray) pageitemsElement;
     	
     	String clientId = null;
     	for ( int i=0; i<clientDetailsJson.size(); ++i) {
     		JsonObject data = clientDetailsJson.get(i).getAsJsonObject();
     		System.out.println("data: " + data);
     		clientId = data.get("id").getAsString();
     		System.out.println("id " + clientId);
     	}
    	
     	//String clientUrlForDefaultSavingsAccount = "https://livetest.encot.net/fineract-provider/api/v1/clients/"+id+"?tenantIdentifier=default";
     	MomoBridge defaultSavingsAccountConfig = this.momoConfigurationRepository.findOneByName("getClientDetailsUrl");
	    String getClientDetailsUrl = defaultSavingsAccountConfig.getValue();
	    clientSearchUrl = getClientDetailsUrl.replace("{clientId}", clientId);
	     
     	final Response clientData = this.momoBridgeService.okHttpMethod(clientSearchUrl, null, "fineract-get", null);
    	 
     	try {
            responseMessage = clientData.body().string();
            }catch (IOException e) {
                LOG.error("error occured in HTTP request-response method.", e);
            }
     	
    	 
    	 
     	
    	JsonObject jsonObject = parser.parse(responseMessage).getAsJsonObject();
        System.out.println("savingsAccountId#### : " + jsonObject);
        
       // String defaultSavingsId = jsonObject.getAsJsonArray("savingsAccountId").getAsString();
    	String defaultSavingsId = jsonObject.get("savingsAccountId").getAsString();
        
      //  String depositUrl = "https://livetest.encot.net/fineract-provider/api/v1/savingsaccounts/"+defaultSavingsId+"/transactions?command=deposit&tenantIdentifier=default";
        MomoBridge depositUrlConfiguration = this.momoConfigurationRepository.findOneByName("depositSavingsUrl");
	    String depositUrl = depositUrlConfiguration.getValue();
	    //String url = clientSearchUrl.getValue();
	    depositUrl = depositUrl.replace("{defaultSavingsId}", defaultSavingsId);
    	 
    	
    	LocalDateTime myDateObj = LocalDateTime.now();
      
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = myDateObj.format(myFormatObj);
        
        String amount = response.get("amount");
        String transaction = "{'transactionDate':'"+formattedDate+"','transactionAmount':'"+amount+"','paymentTypeId':"+paymentType+",'locale':'en','dateFormat':'dd MMMM yyyy'}";
        System.out.println("transaction body ##$$ " + transaction );
        
        Response SavingsResponseMessage = this.momoBridgeService.okHttpMethod(depositUrl, transaction, "fineract", null);
        
        
        try {
            responseMessage = SavingsResponseMessage.body().string();
            }catch (IOException e) {
                LOG.error("error occured in HTTP request-response method.", e);
            }
            System.out.println("deposit successful--- >" + responseMessage);
    	 }
    	 
    	 return responseMessage;
    
     }
    /*********************************************TILL HERE Payment From Encot TO MOMO ************************************/
     
	private String getMomoResponse(final String jsonBody) {
		String apiKey = null;
    	 String url = null;
    	 String uuid = null;
    	 MomoBridge uuidConfig = this.momoConfigurationRepository.findOneByName("uuid");
    	 System.out.println("uuidConfig:"+ uuidConfig);
    	 if(uuidConfig.getValue().equals("null")) { //saved UUID
    		 System.out.println("uuidConfig null");
    		 MomoBridge uuidurl = this.momoConfigurationRepository.findOneByName("uuid_url");
    		 url = uuidurl.getValue();
    		 final Response apiUser = this.momoBridgeService.okHttpMethod(url, null, "uuid", null);
    		
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
    		 Response apiUser = this.momoBridgeService.okHttpMethod(urlValue, null, "getapiuser", null); //done 
    		 
    		 Integer code = apiUser.code();
    		 System.out.println("code: "+ code);
    		 if(code.equals(200)) { //apiuser is ok, create apikey
    			 System.out.println("200: ");
    			 MomoBridge apiKeyUrl = this.momoConfigurationRepository.findOneByName("api_key_url");
    			 url = apiKeyUrl.getValue();
    			 urlValue = url.replace("{uuid}", xReferenceId);
    			 apiUser = this.momoBridgeService.okHttpMethod(urlValue, "", "postapikey", null); //done
    			 
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
        		 apiUser = this.momoBridgeService.okHttpMethod(url, "", "postapiuser", null); //done (NOT WORKING)
        		 
        		 code = apiUser.code();
        		 System.out.println("else code : " + code);
        		 //call this function again
        		 
    		 }
    	 }
    	 MomoBridge token = this.momoConfigurationRepository.findOneByName("token"); 
    	 if(token.getValue().equals("null")){ //also check the expiry of the token
    		 MomoBridge tokenUrl = this.momoConfigurationRepository.findOneByName("create_token_url");
    		 Response tokenResponse = this.momoBridgeService.okHttpMethod(tokenUrl.getValue(), "", "token", null);
    		 
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
         
         MomoTransactionData momoBridge = this.momoBridgeService.validateCreate(jsonBody);
         String transaction = "{'amount': "+momoBridge.getAmount()+",'currency':"+momoBridge.getCurrency()+",'externalId': "+momoBridge.getExternalId()+",'payee': {'partyIdType': 'MSISDN','partyId': '"+momoBridge.getMsisdn()+"'},'payerMessage': '"+momoBridge.getPayerNote()+"','payeeNote': '"+momoBridge.getPayeeNote()+"'}";
         String xReferenceId =  this.momoBridgeService.generateUUID();
         Response transferResponse = this.momoBridgeService.okHttpMethod(transferUrl, transaction, "momo", xReferenceId);
        
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

	@ExceptionHandler({PlatformApiDataValidationException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handlePlatformApiDataValidationException(PlatformApiDataValidationException e) {
    	return PlatformApiDataValidationExceptionMapper.toResponse(e) ;
    }
    
    @ExceptionHandler({UnsupportedParameterException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handleUnsupportedParameterException(UnsupportedParameterException e) {
    	return UnsupportedParameterExceptionMapper.toResponse(e) ;
    }
    
    @ExceptionHandler({SMSBridgeNotFoundException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handleSMSBridgeNotFoundException(SMSBridgeNotFoundException e) {
    	return PlatformResourceNotFoundExceptionMapper.toResponse(e) ;
    }
}
