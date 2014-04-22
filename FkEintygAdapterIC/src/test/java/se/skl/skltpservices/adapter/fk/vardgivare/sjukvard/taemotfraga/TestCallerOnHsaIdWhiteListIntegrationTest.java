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
package se.skl.skltpservices.adapter.fk.vardgivare.sjukvard.taemotfraga;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.skl.skltpservices.adapter.fk.FkIntegrationComponentMuleServer.getAddress;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;

public class TestCallerOnHsaIdWhiteListIntegrationTest extends AbstractTestCase {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(TestCallerOnHsaIdWhiteListIntegrationTest.class);
		
	private static final String DEFAULT_SERVICE_ADDRESS_HTTPS = getAddress("inbound.endpoint.https.eintyg.taemotfraga");

	public TestCallerOnHsaIdWhiteListIntegrationTest() {
		// Only start up Mule once to make the tests run faster...
		// Set to false if tests interfere with each other when Mule is started
		// only once.
		setDisposeContextPerClass(true);
	}

	@Before
	public void doSetUp() throws Exception {
		super.doSetUp();
	}

	@Override
	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml,"+
				"test-caller-on-hsaid-whitelist/hsaid-whitelist-FkIntegrationComponent-common.xml," +
				"services/ReceiveMedicalCertificateQuestion-fk-service.xml," +
				"teststub-services/ReceiveMedicalCertificateQuestion-fk-teststub-service.xml";
	}

	@Test
	public void testCallerIsNotOnHsaIdWhiteListWhenUsingHttpsEndpoint() throws Exception {
		TaEmotFragaTestConsumer fkAsConsumer = new TaEmotFragaTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTPS);
		try {
			fkAsConsumer.taEmotFraga();
			fail("Expected error here!");
		} catch (Exception ex) {
			assertTrue(ex.getMessage().contains("FKADAPT003 Caller was not on the white list of accepted HSA ID's. HSA ID: VardgivareC"));
		}
	}
}
