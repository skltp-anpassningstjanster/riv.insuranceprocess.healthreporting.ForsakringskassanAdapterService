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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarResponseType;
import se.skl.skltpservices.adapter.fk.producer.FkAdapterTestProducerLogger;

public class TaEmotSvarIntegrationTest extends AbstractTestCase {
	
	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("FkIntegrationComponent-config");

	public TaEmotSvarIntegrationTest() {
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
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml,FkIntegrationComponent-config.xml,teststub-services/ReceiveMedicalertificateAnswer-fk-teststub-service.xml";
	}

	@Test
	public void testTaEmotSvar() throws Exception {

		TaEmotSvarTestConsumer fkAsConsumer = new TaEmotSvarTestConsumer(
				"https://localhost:12000/tb/fk/ifv/TaEmotSvar/1/rivtabp20");

		TaEmotSvarResponseType response = fkAsConsumer.taEmotSvar();

		assertNotNull(response);
		
		//Verify http headers are propagated frpm FKAdapter to producer (VP)
		assertEquals(rb.getString("FKADAPTER_HSA_ID"), FkAdapterTestProducerLogger.getLatestSenderId());
		assertEquals(rb.getString("VP_INSTANCE_ID"), FkAdapterTestProducerLogger.getLatestVpInstanceId());

	}

}
