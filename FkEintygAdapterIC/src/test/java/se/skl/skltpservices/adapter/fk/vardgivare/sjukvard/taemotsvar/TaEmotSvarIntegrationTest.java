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
package se.skl.skltpservices.adapter.fk.vardgivare.sjukvard.taemotsvar;

import static org.junit.Assert.*;
import static se.skl.skltpservices.adapter.fk.FkIntegrationComponentMuleServer.getAddress;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarResponseType;
import se.skl.skltpservices.adapter.fk.producer.FkAdapterTestProducerLogger;

public class TaEmotSvarIntegrationTest extends AbstractTestCase {
	
private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("FkIntegrationComponent-config");
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(TaEmotSvarIntegrationTest.class);

	private static final String DEFAULT_SERVICE_ADDRESS_HTTPS = getAddress("inbound.endpoint.https.eintyg.receivemedicalcertificateanswer");
	private static final String DEFAULT_SERVICE_ADDRESS_HTTP = getAddress("inbound.endpoint.http.eintyg.receivemedicalcertificateanswer");

	public TaEmotSvarIntegrationTest() {
		// Only start up Mule once to make the tests run faster...
		// Set to false if tests interfere with each other when Mule is started
		// only once.
		setDisposeContextPerClass(true);
	}

	@Before
	public void doSetUp() throws Exception {
		super.doSetUp();
		FkAdapterTestProducerLogger.resetTestProducerLoggerStaticVariables();
	}

	@Override
	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml,"+
				"FkIntegrationComponent-common.xml,"+
				"services/ReceiveMedicalertificateAnswer-fk-service.xml," +
				"teststub-services/ReceiveMedicalertificateAnswer-fk-teststub-service.xml";
	}

	@Test
	public void testTaEmotSvar_happydays_https() throws Exception {
		
		String fkSenderIdFromClientCert = "VardgivareC";

		TaEmotSvarTestConsumer fkAsConsumer = new TaEmotSvarTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTPS);
		TaEmotSvarResponseType response = fkAsConsumer.taEmotSvar();
		assertNotNull(response);
		
		//Verify http headers are propagated frpm FKAdapter to producer (VP)
		assertEquals(fkSenderIdFromClientCert, FkAdapterTestProducerLogger.getLatestSenderId());
		assertEquals(rb.getString("VP_INSTANCE_ID"), FkAdapterTestProducerLogger.getLatestVpInstanceId());

	}
	
	@Test
	public void testTaEmotSvar_happydays_http() throws Exception {
		
		String fkSenderId = "VardgivareC";

		TaEmotSvarTestConsumer fkAsConsumer = new TaEmotSvarTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTP);
		TaEmotSvarResponseType response = fkAsConsumer.taEmotSvar(fkSenderId);
		assertNotNull(response);
		
		//Verify http headers are propagated frpm FKAdapter to producer (VP)
		assertEquals(fkSenderId, FkAdapterTestProducerLogger.getLatestSenderId());
		assertEquals(rb.getString("VP_INSTANCE_ID"), FkAdapterTestProducerLogger.getLatestVpInstanceId());

	}
	
	@Test
	public void testTaEmotSvar_http_no_sender_id() throws Exception {
		
		String fkSenderId = null;

		TaEmotSvarTestConsumer fkAsConsumer = new TaEmotSvarTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTP);
		
		try {
			fkAsConsumer.taEmotSvar(fkSenderId);
			fail("Expected exception");
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("No certificate was found in request, therefore sender is not trusted"));
		}
	}
}
