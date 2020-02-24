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

package org.fineract.messagegateway.sms.providers.impl.wirepick.smsgateway;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



import org.fineract.messagegateway.sms.providers.impl.wirepick.smsgateway.Utility.*;
import org.fineract.messagegateway.sms.providers.impl.wirepick.smsgateway.model.*;

public class WirepickSMS {

	// this guy calls the MsgStatus method
	public MsgStatus SendGETSMS(WpkClientConfig clientConfig) throws Exception {
		if(clientConfig == null)
			throw new NullPointerException() ;
		String httpUrl = Settings.HTTPparameters(clientConfig) ; 
		if(httpUrl != null && httpUrl.startsWith(Settings.HOST))
		{
			//return HttpUrls.sendByUrlHttpConnection(httpUrl) ;
		}
		throw new Exception("Could not do stuff  :( " );
		//return null;
	}

	// avoiding to call the MsgStatus guy as MsgStatus works well with json responses
	public static String SendGETSMS2(WpkClientConfig clientConfig) throws Exception	{
		if(clientConfig == null)
			throw new NullPointerException() ;
		String httpUrl = Settings.HTTPparameters(clientConfig) ;
		if(httpUrl != null && httpUrl.startsWith(Settings.HOST))
		{
			return HttpUrls.sendByUrlHttpConnection(httpUrl) ;
		}
		throw new Exception("Could not do stuff  :( " );
		//return null;
	}

	// this guy calls the MsgStatus method too - if any issues avoid him
	public MsgStatus SendPOSTSMS(WpkClientConfig clientConfig) throws Exception	{
		if(clientConfig == null)
			throw new NullPointerException() ;
			String httpUrl = Settings.HOST ;
			NameValuePair[] valuePairs = Settings.GetParameters(clientConfig) ;
			//System.out.println("SendPOSTSMS of WirepickSMS.java \n the array in use is " + Arrays.toString(valuePairs));
		//return HttpUrls.sendByPostMethod(httpUrl, valuePairs, null);
		throw new Exception("Could not do stuff  :( " );
	}


	public static String SendPOSTSMS2(TrueAfricanConfig clientConfig) throws Exception
	{
		if(clientConfig == null)
			throw new NullPointerException() ;
		String httpUrl = Settings.HOST ;

		String[] msisdnArray = new String[] clientConfig.getMsisdn();
		System.out.println(msisdnArray[0]); //prints "name"

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		HashMap<String, String[]> body1 = new HashMap<String, String[]>();
		Map<String, String> body2 = new HashMap<>();

		//items.put("msisdn", clientConfig.getMsisdn());
		body1.put("msisdn", msisdnArray[0]);

		body2.put("message", clientConfig.getMessage());
		body2.put("username", clientConfig.getUsername());
		body2.put("password", clientConfig.getPassword());

		System.out.println(" this below is the json built ");
		gson.toJson(body1+body2, System.out);

		return HttpUrls.sendByPostMethod2(httpUrl, gson.toJson(items));

		//throw new Exception("Could not do stuff " );
	}
}
