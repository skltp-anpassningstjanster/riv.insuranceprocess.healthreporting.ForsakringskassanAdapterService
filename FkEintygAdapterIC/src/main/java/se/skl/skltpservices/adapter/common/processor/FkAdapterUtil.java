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
import org.mule.api.config.MuleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FkAdapterUtil {
	
	private static final Logger log = LoggerFactory.getLogger(FkAdapterUtil.class);
	
	/*
	 * Http header x-vp-sender-id, for FK adapter to use when acting consumer towards VP. 
	 * Http heaeder x-vp-instance-id, for FK adapter to use when acting consumer towards VP.
	 * 
	 * These two headers are dependent on each other in a way that when using x-vp-sender-id
	 * against VP, VP will check for a valid x-vp-instance-id.
	 */
	public static final String X_VP_SENDER_ID = "x-vp-sender-id";
	public static final String X_VP_INSTANCE_ID = "x-vp-instance-id";

	/*
	 * External representation of the FK sender id
	 * 
	 * Http header x-fk-sender-id, mandatory header to be used when using http in FK endpoints (TaEmotSvar, TaEmotFraga)
	 * The value of this header is forward in request to VP using http header x-vp-sender-id
	 */
	public static final String X_FK_SENDER_ID = "x-fk-sender-id";
	
	/*
	 * Internal representation of the FK sender id
	 */
	public static final String FK_SENDER_ID = "fkSenderId";
	
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
}
