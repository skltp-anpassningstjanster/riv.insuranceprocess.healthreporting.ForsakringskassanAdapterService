/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skl.skltpservices.adapter.common.processor;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.mule.api.config.MuleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FkAdapterUtil {
	
	private static final Logger log = LoggerFactory.getLogger(FkAdapterUtil.class);
	
	public static final String REMOTE_ADDR = MuleProperties.MULE_REMOTE_CLIENT_ADDRESS;
	
	/*
	 * Generic soap fault template, just use String.format(SOAP_FAULT, message);
	 */
	private final static String SOAP_FAULT = 
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
			"  <soapenv:Header/>" + 
			"  <soapenv:Body>" + 
			"    <soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
			"      <faultcode>soap:Server</faultcode>\n" + 
			"      <faultstring>%s</faultstring>\n" +
			"    </soap:Fault>" + 
			"  </soapenv:Body>" + 
			"</soapenv:Envelope>";
	
	/**
     * Escapes the characters in a String using XML entities.
     * 
	 * @param string
	 * @return escaped string
	 */
	public static final String escape(final String string) {
		return StringEscapeUtils.escapeXml(string);
	}
	
	/**
	 * Generate soap 1.1 fault containing the value of parameter cause.
	 * 
	 * @param cause
	 * @return soap 1.1 fault with cause
	 */
	public static final String generateSoap11FaultWithCause(final String cause) {
		return String.format(SOAP_FAULT, escape(cause));
	}
	
	/**
	 * Generate FKADAPT001 error calling service producer.
	 * 
	 * @param cause
	 * @return soap 1.1 fault with cause
	 */
	public static final String generateErrorCallingServiceProducerSoapFaultWithCause(final String cause) {	
		return String.format(SOAP_FAULT, escape("FKADAPT001 Exception when calling the service producer: " + cause));
	}

	/**
	 * Check if the entry provided by the caller is on accepted list of entries in whitelist. False
	 * is always returned in case no whitelist exist or provided call entry is empty.
	 * 
	 * @param callerEntry The callers entry to match againts whitelist entries
	 * @param whiteList The comma separated whitelist of entries to compare againts 
	 * @return true if caller entry is on whitelist
	 */
	public static boolean isCallerOnWhiteList(String callerEntry, String whiteList) {
		
		log.debug("Check if caller {} is in whitelist...", callerEntry);

		if (StringUtils.isBlank(callerEntry)) {
			log.warn("A potential empty ip address from the caller");
			return false;
		}
		
		if (StringUtils.isBlank(whiteList)) {
			log.error("An empty whitelist is used when checking if caller is on whitelist, not ok. Check will return false!");
			return false;
		}
		
		for (String whiteListEntry : whiteList.split(",")) {
			if(callerEntry.equals(whiteListEntry.trim())){
				log.debug("Caller matches entry in white list, ok");
				return true;
			}
		}

		log.warn("Caller was not on the white list of accepted entries. Caller entry: {}, accepted entries in whitelist: {}", callerEntry, whiteList);
		return false;
	}
}
