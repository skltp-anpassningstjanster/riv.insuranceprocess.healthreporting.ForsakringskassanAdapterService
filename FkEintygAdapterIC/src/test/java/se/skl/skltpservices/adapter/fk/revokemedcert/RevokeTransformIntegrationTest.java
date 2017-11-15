/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
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
package se.skl.skltpservices.adapter.fk.revokemedcert;

import static org.junit.Assert.assertEquals;
import static se.skl.skltpservices.adapter.fk.FkIntegrationComponentMuleServer.getAddress;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.inera.ifv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionResponseType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.skltpservices.adapter.fk.producer.FkAdapterTestProducerLogger;

public class RevokeTransformIntegrationTest extends AbstractTestCase {
	
	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("FkIntegrationComponent-config");
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(RevokeTransformIntegrationTest.class);

	private static final String DEFAULT_SERVICE_ADDRESS_HTTPS = getAddress("inbound.endpoint.https.eintyg.sendmedicalcertificatequestion.revoke");
	private static final String DEFAULT_SERVICE_ADDRESS_HTTP = getAddress("inbound.endpoint.http.eintyg.sendmedicalcertificatequestion.revoke");
	
	@Before
	public void doSetUp() throws Exception {
		super.doSetUp();
		FkAdapterTestProducerLogger.resetTestProducerLoggerStaticVariables();
	}

	@Override
	protected String getConfigResources() {
		return 	"soitoolkit-mule-jms-connector-activemq-embedded.xml," +
				"FkIntegrationComponent-common.xml," +
				"services/Revoke-fk-service.xml," +
			    "teststub-services/RevokeMedicalCertificate-fk-teststub-service.xml";
	}

	@Test
	public void sendMCQMakulering_happydays_https() throws Exception {
		RevokeTransformTestConsumer consumer = new RevokeTransformTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTPS);
		SendMedicalCertificateQuestionResponseType response = consumer.sendMCQuestion(Amnetyp.MAKULERING_AV_LAKARINTYG, "Kalle");
		Thread.currentThread().sleep(1000);
		
		assertEquals(response.getResult().getResultCode(), ResultCodeEnum.OK);
		
		//Verify http headers are propagated frpm FKAdapter to producer (VP) when revoke is triggered
		assertEquals(rb.getString("FKADAPTER_HSA_ID"), FkAdapterTestProducerLogger.getLatestSenderId());
		assertEquals(rb.getString("VP_INSTANCE_ID"), FkAdapterTestProducerLogger.getLatestVpInstanceId());
	}
	
	@Test
	public void sendMCQMakulering_happydays_http() throws Exception {
		RevokeTransformTestConsumer consumer = new RevokeTransformTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTP);
		SendMedicalCertificateQuestionResponseType response = consumer.sendMCQuestion(Amnetyp.MAKULERING_AV_LAKARINTYG, "Kalle");
		Thread.currentThread().sleep(1000);
		
		assertEquals(response.getResult().getResultCode(), ResultCodeEnum.OK);
		
		//Verify http headers are propagated frpm FKAdapter to producer (VP) when revoke is triggered
		assertEquals(rb.getString("FKADAPTER_HSA_ID"), FkAdapterTestProducerLogger.getLatestSenderId());
		assertEquals(rb.getString("VP_INSTANCE_ID"), FkAdapterTestProducerLogger.getLatestVpInstanceId());
	}
	
	@Test
	public void sendMCQOvrigt_happydays_https() throws Exception {
		RevokeTransformTestConsumer consumer = new RevokeTransformTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTPS);
		SendMedicalCertificateQuestionResponseType response = consumer.sendMCQuestion(Amnetyp.OVRIGT, "Kalle");
		Thread.currentThread().sleep(1000);
		
		assertEquals(response.getResult().getResultCode(), ResultCodeEnum.OK);
	}
	
	@Test
	public void sendMCQOvrigt_happydays_http() throws Exception {
		RevokeTransformTestConsumer consumer = new RevokeTransformTestConsumer(DEFAULT_SERVICE_ADDRESS_HTTP);
		SendMedicalCertificateQuestionResponseType response = consumer.sendMCQuestion(Amnetyp.OVRIGT, "Kalle");
		Thread.currentThread().sleep(1000);
		
		assertEquals(response.getResult().getResultCode(), ResultCodeEnum.OK);
	}

}
