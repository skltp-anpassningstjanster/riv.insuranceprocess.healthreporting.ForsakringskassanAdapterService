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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.skl.skltpservices.adapter.fk.FkIntegrationComponentMuleServer.getAddress;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaResponseType;
import se.skl.skltpservices.adapter.fk.producer.FkAdapterTestProducerLogger;

public class TaEmotFragaIntegrationTest extends AbstractTestCase {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(TaEmotFragaIntegrationTest.class);
	
	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("FkIntegrationComponent-config");
	
	private static final String DEFAULT_SERVICE_ADDRESS_HTTPS = getAddress("inbound.endpoint.https.eintyg.taemotfraga");
	private static final String DEFAULT_SERVICE_ADDRESS_HTTP = getAddress("inbound.endpoint.http.eintyg.taemotfraga");

	public TaEmotFragaIntegrationTest() {
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
				"FkIntegrationComponent-common.xml," +
				"services/ReceiveMedicalCertificateQuestion-fk-service.xml," +
				"teststub-services/ReceiveMedicalCertificateQuestion-fk-teststub-service.xml";
	}

	@Test
	public void testTaEmotFraga_happydays_http() throws Exception {
		TaEmotFragaTestConsumer fkAsConsumer = new TaEmotFragaTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTP);
		TaEmotFragaResponseType response = fkAsConsumer.taEmotFraga();
		assertNotNull(response);
		
		//Verify http headers are propagated frpm FKAdapter to producer (VP)
		assertEquals(rb.getString("FKADAPTER_HSA_ID"), FkAdapterTestProducerLogger.getLatestSenderId());
		assertEquals(rb.getString("VP_INSTANCE_ID"), FkAdapterTestProducerLogger.getLatestVpInstanceId());

	}
	
	@Test
	public void testTaEmotFraga_happydays_https() throws Exception {
		TaEmotFragaTestConsumer fkAsConsumer = new TaEmotFragaTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTPS);
		TaEmotFragaResponseType response = fkAsConsumer.taEmotFraga();
		assertNotNull(response);
		
		//Verify http headers are propagated frpm FKAdapter to producer (VP)
		assertEquals(rb.getString("FKADAPTER_HSA_ID"), FkAdapterTestProducerLogger.getLatestSenderId());
		assertEquals(rb.getString("VP_INSTANCE_ID"), FkAdapterTestProducerLogger.getLatestVpInstanceId());

	}

}
