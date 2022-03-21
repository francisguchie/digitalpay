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
import org.fineract.messagegateway.sms.repository.SMSBridgeRepository;
import org.fineract.messagegateway.sms.serialization.SmsBridgeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squareup.okhttp.Credentials;

@Service(value = "apacheServiceBridge")
public class MomoBridgeService {
	private static final Logger LOG = LoggerFactory.getLogger(MomoBridgeService.class);
	
	@Autowired
	public MomoBridgeService() {
		
	}
	
	public String okHttpMethod(String url, String body) {
		String responseMessage = null;
		OkHttpClient client = new OkHttpClient();
         HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
         String urlokhttp = urlBuilder.build().toString();
         String credential = Credentials.basic("rahul", "password");
         Request request = null;
         if(body == null) {
         
        	 request = new Request.Builder().header("Content-Type", "application/json")
        		 .header("Authorization", credential).url(urlokhttp).get().build();
         }
         else if (body != null){
         final MediaType mediaType = MediaType.parse("application/json");
         RequestBody requestBody = RequestBody.create(body, mediaType);
        	  request = new Request.Builder().header("Content-Type", "application/json")
            		 .header("Authorization", credential).url(urlokhttp).post(requestBody).build();
         }
         
         Response response;
         Integer responseCode = 0;
         try {
             response = client.newCall(request).execute();
             responseCode = response.code();
             responseMessage = response.body().string();
         } catch (IOException e) {
             LOG.error("error occured in HTTP request-response method.", e);
         }
         
		return responseMessage;
	}

}
