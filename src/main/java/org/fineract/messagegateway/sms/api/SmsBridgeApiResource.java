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
import java.util.Date;

import okhttp3.Request;
import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.exception.UnsupportedParameterException;
import org.fineract.messagegateway.helpers.ApiGlobalErrorResponse;
import org.fineract.messagegateway.helpers.PlatformApiDataValidationExceptionMapper;
import org.fineract.messagegateway.helpers.PlatformResourceNotFoundExceptionMapper;
import javax.ws.rs.core.Context;
import okhttp3.OkHttpClient;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import com.google.gson.JsonSyntaxException;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import okhttp3.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fineract.messagegateway.helpers.UnsupportedParameterExceptionMapper;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.exception.SMSBridgeNotFoundException;
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

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//

//
@RestController
@RequestMapping("/smsbridges")
public class SmsBridgeApiResource {

	private final SMSBridgeService smsBridgeService;
	private final MomoBridgeService momoBridgeService;
      
private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name", "systemDefined"));
private static final Logger LOG = LoggerFactory.getLogger(SmsBridgeApiResource.class);
	
	@Autowired
    public SmsBridgeApiResource(final SMSBridgeService smsBridgeService, final MomoBridgeService momoBridgeService) {
		this.smsBridgeService = smsBridgeService ;
		this.momoBridgeService = momoBridgeService;
              
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
         String url = "https://release160.guchietech.pw/fineract-provider/api/v1/clients?sqlSearch=c.account_No="+accountNumber+"&tenantIdentifier=default";
       
         String responseMessage = this.momoBridgeService.okHttpMethod(url, null);
     
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
                	 String clientResponseMessage = this.momoBridgeService.okHttpMethod(clientUrl, null);
                	 System.out.println("*****");
                	 System.out.println("clientResponseMessage++ " + clientResponseMessage);
                	 JsonElement loanElement = parser.parse(clientResponseMessage);
                 	JsonObject loanObject = loanElement.getAsJsonObject();
                 	String savingsId= loanObject.get("savingsAccountId").getAsString();
                 	System.out.println("savingsId: " +  savingsId);
       
                 	LocalDateTime myDateObj = LocalDateTime.now();
                    System.out.println("Before formatting: " + myDateObj);
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                    String formattedDate = myDateObj.format(myFormatObj);
                 	String transaction = "{'transactionDate':'"+formattedDate+"','transactionAmount':'"+amount+"','paymentTypeId':1,'locale':'en','dateFormat':'dd MMMM yyyy'}";
                 	String savingsUrl ="https://release160.guchietech.pw/fineract-provider/api/v1/savingsaccounts/"+savingsId+"/transactions?command=deposit&tenantIdentifier=default";
                 	String SavingsResponseMessage = this.momoBridgeService.okHttpMethod(savingsUrl, transaction);
                }
            }
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return  responseMessage;
    }
     
     @RequestMapping(value = "/momoDeposit", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
     public String postDepositMomo(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
     		@RequestHeader("mobileNo")  String mobileNo,
     		@RequestHeader("amount")  String amount) {
    	 
    	 RequestBody requestBody = null;
        // String url = "https://release160.guchietech.pw/fineract-provider/api/v1/loans?sqlSearch=c.account_No="+accountNumber+"&tenantIdentifier=default";

         String result = null;
     	return result;
     }

	
   /*  @RequestMapping(value = "/getLinkSavingsId", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
     public String getLinkedSavingsAccount(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
 @Context final UriInfo uriInfo) {
        // final Collection<CodeData> codes = this.readPlatformService.retrieveAllCodes();

         final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
       //  return this.toApiJsonSerializer.serialize(settings, codes, RESPONSE_DATA_PARAMETERS);
     }
    */
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
