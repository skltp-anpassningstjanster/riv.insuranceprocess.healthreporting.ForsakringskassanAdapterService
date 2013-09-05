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
package se.skl.skltpservices.adapter.fk.revokemedcert;

import org.junit.Before;
import org.junit.Test;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;


import se.skl.riv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;

public class RevokeTransformIntegrationTest extends AbstractTestCase {

	@Before
	public void doSetUp() throws Exception {
		super.doSetUp();
	}

	@Override
	protected String getConfigResources() {
		return "FkIntegrationComponent-common.xml,Revoke-fk-service.xml,teststub-services/RevokeMedicalCertificate-fk-teststub-service.xml";
	}

	@Test
	public void testSendMCQMakulering() throws Exception {
		RevokeTransformTestConsumer consumer = new RevokeTransformTestConsumer(
				"http://localhost:11000/tb/eintyg/revoke/SendMedicalCertificateQuestion/1/rivtabp20");

		SendMedicalCertificateQuestionResponseType response = consumer.sendMCQuestion(Amnetyp.MAKULERING_AV_LAKARINTYG, "Kalle");

		Thread.currentThread().sleep(1000);
		
		assertEquals(response.getResult().getResultCode(), ResultCodeEnum.OK);
	}
	
	@Test
	public void testSendMCQOvrigt() throws Exception {
		RevokeTransformTestConsumer consumer = new RevokeTransformTestConsumer(
				"http://localhost:11000/tb/eintyg/revoke/SendMedicalCertificateQuestion/1/rivtabp20");

		SendMedicalCertificateQuestionResponseType response = consumer.sendMCQuestion(Amnetyp.OVRIGT, "Kalle");

		Thread.currentThread().sleep(1000);
		
		assertEquals(response.getResult().getResultCode(), ResultCodeEnum.OK);
	}

}
