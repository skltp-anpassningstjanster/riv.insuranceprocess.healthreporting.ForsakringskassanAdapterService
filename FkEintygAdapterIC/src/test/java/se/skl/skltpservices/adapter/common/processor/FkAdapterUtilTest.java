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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class FkAdapterUtilTest {

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
}
