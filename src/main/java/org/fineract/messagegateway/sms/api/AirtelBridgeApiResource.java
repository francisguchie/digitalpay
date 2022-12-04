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
import javax.ws.rs.core.Context;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.OkHttpClient;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import com.google.gson.JsonSyntaxException;
import org.json.JSONArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import okhttp3.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

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
public class AirtelBridgeApiResource {

	private final SMSBridgeService smsBridgeService;
	private final MomoBridgeService momoBridgeService;
	private final MomoConfigurationRepository momoConfigurationRepository;
	private final MomoTransactionsRepository momoTransactionsRepository;

private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name", "systemDefined"));
private static final Logger LOG = LoggerFactory.getLogger(SmsBridgeApiResource.class);

	@Autowired
    public AirtelBridgeApiResource(final SMSBridgeService smsBridgeService, final MomoBridgeService momoBridgeService, final MomoConfigurationRepository momoConfigurationRepository,
    		final MomoTransactionsRepository momoTransactionsRepository) {
		this.smsBridgeService = smsBridgeService ;
		this.momoBridgeService = momoBridgeService;
		this.momoConfigurationRepository = momoConfigurationRepository;
		this.momoTransactionsRepository = momoTransactionsRepository;
    }

/*********************************************FROM HERE Payment From Encot TO MOMO ************************************/
     @RequestMapping(value = "/payment", method = RequestMethod.POST, consumes = {"application/json", "text/xml;charset=utf-8"}, produces = {"application/json", "text/json;charset=utf-8"})
     public ResponseEntity<Void>  postDepositMomo(@RequestBody String json, HttpServletResponse httpResponse) {
         System.out.println("PAYMENT STANDARD API");
    	 String responseMessage = null;

    	 Map<String, String> response = ParserUtil.processXML(json);
	     String transactionId = response.get("externaltransactionid");
	     String momoTransactionId = response.get("transactionid");
	     String paymentType = response.get("paymentTypeId");

	     String status = response.get("status");

	     String clientId = null;
         clientId = response.get("narration"); //defaultSavingsId is passed in Message

         if (clientId != null) {
    		    System.out.println("savings account NOT blank");
    		    //String depositUrl = "https://livetest.encot.net/fineract-provider/api/v1/savingsaccounts/{clientId}/transactions?command=deposit&tenantIdentifier=default";

    		    String defaultSavingsId = null;

    		    MomoBridge defaultSavingsAccountConfig = this.momoConfigurationRepository.findOneByName("getClientDetailsUrl");
	            String getClientDetailsUrl = defaultSavingsAccountConfig.getValue();
	            String clientSearchUrl = getClientDetailsUrl.replace("{clientId}", clientId);

             	final Response clientData = this.momoBridgeService.okHttpMethod(clientSearchUrl, null, "fineract-get", null);
             	 Integer responseCode = clientData.code();

             	try {
             	    if(responseCode != 200){
                        System.out.println("Deposit Failed: " +  clientData.body().string());
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        	        }
                    responseMessage = clientData.body().string();

                }catch (IOException e ) {
                   LOG.error("error occured in HTTP request-response method.", e);
                   return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                 }


                 try{
                     JsonParser parser = new JsonParser();
              	JsonObject jsonObject = parser.parse(responseMessage).getAsJsonObject();
              	defaultSavingsId = jsonObject.get("savingsAccountId").getAsString();
                 }
                 catch(NullPointerException e){
                     LOG.error("error occured in HTTP request-response method.", e);
                   return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                 }



                // String defaultSavingsId = jsonObject.getAsJsonArray("savingsAccountId").getAsString();





    		    MomoBridge depositUrlConfig = this.momoConfigurationRepository.findOneByName("depositSavingsUrl");
    	        String url = depositUrlConfig.getValue();
    	        String depositUrl = url.replace("{defaultSavingsId}", defaultSavingsId);

    		    LocalDateTime myDateObj = LocalDateTime.now();

    	        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    	        String formattedDate = myDateObj.format(myFormatObj);

    	        String amount = response.get("amount");

    	        String transaction = "{'transactionDate':'"+formattedDate+"','transactionAmount':'"+amount+"','paymentTypeId':"+paymentType+",'locale':'en','dateFormat':'dd MMMM yyyy'}";


    	        Response SavingsResponseMessage = this.momoBridgeService.okHttpMethod(depositUrl, transaction, "fineract", null);

    	        responseCode = SavingsResponseMessage.code();




    	        try {
    	            if(responseCode != 200){
                        System.out.println("Deposit Failed: " +  SavingsResponseMessage.body().string());
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        	        }
    	        else {
                    System.out.println("Deposit successful--- >" + responseMessage);
    	        }
    	       //    return responseMessage = SavingsResponseMessage.body().string();
    	           return new ResponseEntity<>(HttpStatus.OK);
    	            }catch (IOException e) {
    	                LOG.error("error occured in HTTP request-response method.", e);
    	            }
    	    	 }
         return new ResponseEntity<>(HttpStatus.OK);
     }
     
     
}

