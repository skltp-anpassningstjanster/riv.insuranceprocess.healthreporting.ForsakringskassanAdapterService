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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;


public class FkAdapterUtilTest {
	
	public static final String IP_WHITE_LIST="127.0.0.1,127.0.0.2,127.0.0.3";
	public static final String REMOTE_ADDRESS = "/127.0.0.1:52440";
	
	final static String CORRECT_FORMATED_SOAP_FAULT = 
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
			"  <soapenv:Header/>" + 
			"  <soapenv:Body>" + 
			"    <soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
			"      <faultcode>soap:Server</faultcode>\n" + 
			"      <faultstring>%s</faultstring>\n" +
			"    </soap:Fault>" + 
			"  </soapenv:Body>" + 
			"</soapenv:Envelope>";

	@Test
	public void transformToSoapFault_ok() {
		String cause = "FKADAPT001 Exception when calling the service producer: An error occured";
		String expectedResult = String.format(CORRECT_FORMATED_SOAP_FAULT, cause);
		
		String actualResult = FkAdapterUtil.generateSoap11FaultWithCause(cause);
		
		assertNotNull(actualResult);
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void extractIpAdressFromRemoteClientAddress() {
		
		MuleMessage message = mock(MuleMessage.class);
		when(message.getProperty(FkAdapterUtil.REMOTE_ADDR, PropertyScope.INBOUND)).thenReturn(REMOTE_ADDRESS);
		
		String ipAddress = FkAdapterUtil.extractIpAddress(message);
		assertEquals("127.0.0.1", ipAddress);
	}
	
	@Test
	public void isCallerOnWhiteListOk(){
				
		boolean callerOnWhiteList = FkAdapterUtil.isCallerOnWhiteList("127.0.0.1", IP_WHITE_LIST);
		assertTrue(callerOnWhiteList);
	}
	
	@Test
	public void isCallerOnWhiteListOkWhenWhiteListContainsLeadingWiteSpaces(){
		
		final String WHITE_LIST_WITH_WHITE_SPACE="127.0.0.1, 127.0.0.2";
		boolean callerOnWhiteList = FkAdapterUtil.isCallerOnWhiteList("127.0.0.2 ", WHITE_LIST_WITH_WHITE_SPACE);
		assertTrue(callerOnWhiteList);
	}		
	
	@Test
	public void isCallerOnWhiteListIpDoesNotMatch(){
				
		boolean callerOnWhiteList = FkAdapterUtil.isCallerOnWhiteList("126.0.0.1", IP_WHITE_LIST);
		assertFalse(callerOnWhiteList);
	}
	
	@Test
	public void isCallerOnWhiteListMatchesSubdomain(){
		
		String whiteListOfSubDomains = "127.0.0,127.0.1.0";		
		boolean callerOnWhiteList = FkAdapterUtil.isCallerOnWhiteList("127.0.0.1", whiteListOfSubDomains);
		assertTrue(callerOnWhiteList);
	}
	
	@Test
	public void isCallerOnWhiteListDoesNotMatchSubdomain(){
		
		String whiteListOfSubDomains = "127.0.0,127.0.1";		
		boolean callerOnWhiteList = FkAdapterUtil.isCallerOnWhiteList("127.0.2.1", whiteListOfSubDomains);
		assertFalse(callerOnWhiteList);
	}
	
	@Test
	public void isCallerOnWhiteListReturnsFalseWhenIpAddressIsEmpty(){		
		
		String ipAddress = "";
		assertFalse(FkAdapterUtil.isCallerOnWhiteList(ipAddress, IP_WHITE_LIST));
	}
	
	@Test
	public void isCallerOnWhiteListReturnsFalseWhenIpAddressIsNull(){		
		
		String ipAddress = null;
		assertFalse(FkAdapterUtil.isCallerOnWhiteList(ipAddress, IP_WHITE_LIST));
	}
	
	@Test
	public void isCallerOnWhiteListReturnsFalseWhenWhiteListIsEmpty(){
			
		String whiteList = "";
		assertFalse(FkAdapterUtil.isCallerOnWhiteList("127.0.0.1", whiteList));
	}
	
	@Test
	public void isCallerOnWhiteListReturnsFalseWhenWhiteListIsNull(){
			
		String whiteList = null;
		assertFalse(FkAdapterUtil.isCallerOnWhiteList("127.0.0.1", whiteList));
	}

}
