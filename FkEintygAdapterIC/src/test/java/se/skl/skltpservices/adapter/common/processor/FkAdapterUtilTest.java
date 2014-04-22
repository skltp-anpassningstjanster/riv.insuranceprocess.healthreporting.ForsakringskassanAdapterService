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

import org.junit.Test;


public class FkAdapterUtilTest {
	
	public static final String HSA_ID_WHITE_LIST="HSAID-1,HSAID-2,HSAID-3";

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
	public void isCallerOnWhiteListOk(){
				
		boolean callerOnWhiteList = FkAdapterUtil.isCallerOnWhiteList("HSAID-2", HSA_ID_WHITE_LIST);
		assertTrue(callerOnWhiteList);
	}
	
	@Test
	public void isCallerOnWhiteListOkWhenWhiteListContainsLeadingWiteSpaces(){
		
		final String WHITE_LIST_WITH_WHITE_SPACE="HSAID-1, HSAID-2";
		boolean callerOnWhiteList = FkAdapterUtil.isCallerOnWhiteList("HSAID-2", WHITE_LIST_WITH_WHITE_SPACE);
		assertTrue(callerOnWhiteList);
	}		
	
	@Test
	public void isCallerOnWhiteListHsaIdDoesNotMatch(){
		assertFalse(FkAdapterUtil.isCallerOnWhiteList("HSAID-UNKOWN", HSA_ID_WHITE_LIST));
		assertFalse(FkAdapterUtil.isCallerOnWhiteList("HSAID", HSA_ID_WHITE_LIST));
		assertFalse(FkAdapterUtil.isCallerOnWhiteList("ID-1", HSA_ID_WHITE_LIST));
	}
	
	@Test
	public void isCallerOnWhiteListReturnsFalseWhenHsaIdAddressIsEmpty(){		
		
		String hsaId = "";
		assertFalse(FkAdapterUtil.isCallerOnWhiteList(hsaId, HSA_ID_WHITE_LIST));
	}
	
	@Test
	public void isCallerOnWhiteListReturnsFalseWhenHsaIdAddressIsNull(){		
		
		String hsaId = null;
		assertFalse(FkAdapterUtil.isCallerOnWhiteList(hsaId, HSA_ID_WHITE_LIST));
	}
	
	@Test
	public void isCallerOnWhiteListReturnsFalseWhenWhiteListIsEmpty(){
			
		String whiteList = "";
		assertFalse(FkAdapterUtil.isCallerOnWhiteList("HSAID-1", whiteList));
	}
	
	@Test
	public void isCallerOnWhiteListReturnsFalseWhenWhiteListIsNull(){
			
		String whiteList = null;
		assertFalse(FkAdapterUtil.isCallerOnWhiteList("HSAID-1", whiteList));
	}

}
