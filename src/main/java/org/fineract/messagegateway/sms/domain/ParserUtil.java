package org.fineract.messagegateway.sms.domain;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

	

public class ParserUtil {

		public static Map<String, String> processXML(String xml) {
			Map<String, String> response = new HashMap<>();
			
			try {
				boolean isMomoTransIdSet = false; boolean isReqIdSet = false; boolean isStatusSet = false;
				boolean isAmountSet = false; boolean isNarrationSet = false; boolean isAccountHolderSet = false;
				boolean isPaymentstatus = false; boolean isFinTrans = false;
				boolean isMsisdn = false; boolean isUsername = false; boolean isPassword = false; 

				XMLInputFactory factory = XMLInputFactory.newInstance();
				XMLEventReader eventReader = factory.createXMLEventReader(new ByteArrayInputStream(xml.getBytes()));

				while(eventReader.hasNext()) {
					XMLEvent event = eventReader.nextEvent();

					switch(event.getEventType()) {

					case XMLStreamConstants.START_ELEMENT:
						StartElement startElement = event.asStartElement();
						String qName = startElement.getName().getLocalPart();

						if (qName.equalsIgnoreCase("transactionid") || qName.equalsIgnoreCase("mobtxnid") || qName.equalsIgnoreCase("txnid")) {
							isMomoTransIdSet = true;
						} else if (qName.equalsIgnoreCase("externaltransactionid") || qName.equalsIgnoreCase("exttrid")) {
							isReqIdSet = true;
						} else if (qName.equalsIgnoreCase("status") || qName.equalsIgnoreCase("txtstatus") || qName.equalsIgnoreCase("txnstatus")) { 
							isStatusSet = true;
						} else if (qName.equalsIgnoreCase("amount")) {
							isAmountSet = true;
						} else if (qName.equalsIgnoreCase("message") || qName.equalsIgnoreCase("reference")) {
							isNarrationSet = true;
						} else if (qName.equalsIgnoreCase("accountholderid")) {
							isAccountHolderSet = true;
						} else if (qName.equalsIgnoreCase("paymentstatus")) {
							isPaymentstatus = true;
						} else if (qName.equalsIgnoreCase("financialtransactionid")) {
							isFinTrans = true;
						} else if (qName.equalsIgnoreCase("msisdn")) {
							isMsisdn = true;
						} else if (qName.equalsIgnoreCase("username")) {
							isUsername = true;
						} else if (qName.equalsIgnoreCase("password")) {
							isPassword = true;
						}
						break;

					case XMLStreamConstants.CHARACTERS:
						Characters characters = event.asCharacters();
						if(isMomoTransIdSet) {
							response.put("transactionid", characters.getData());
							isMomoTransIdSet = false;
						}
						if(isReqIdSet) {
							response.put("externaltransactionid", characters.getData());
							isReqIdSet = false;
						}
						if(isStatusSet) {
							response.put("status", characters.getData());
							isStatusSet = false;
						}
						
						if(isAmountSet) {
							response.put("amount", characters.getData());
							isAmountSet = false;
						}
						
						if(isNarrationSet) {
							response.put("message", characters.getData());
							isNarrationSet = false;
						}
						
						if(isAccountHolderSet) {
							response.put("accountholderid", characters.getData());
							isAccountHolderSet = false;
						}
						
						if(isPaymentstatus) {
							response.put("paymentstatus", characters.getData());
							isPaymentstatus = false;
						}
						
						if(isFinTrans) {
							response.put("financialtransactionid", characters.getData());
							isFinTrans = false;
						}
						
						if(isMsisdn) {
							response.put("msisdn", characters.getData());
							isMsisdn = false;
						}
						
						if(isUsername) {
							response.put("username", characters.getData());
							isUsername = false;
						}
						
						if(isPassword) {
							response.put("password", characters.getData());
							isPassword = false;
						}
						break;
					} 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}
		
		public static String extractPhoneNumber(String request) {
			return request.split(":")[1].split("/")[0];
		}
}