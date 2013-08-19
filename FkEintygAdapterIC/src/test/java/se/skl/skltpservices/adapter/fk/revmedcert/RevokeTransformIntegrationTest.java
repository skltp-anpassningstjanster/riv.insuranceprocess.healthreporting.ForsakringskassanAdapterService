package se.skl.skltpservices.adapter.fk.revmedcert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.soitoolkit.commons.mule.test.AbstractTestCase;

import se.skl.riv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;

public class RevokeTransformIntegrationTest extends AbstractTestCase {

	@BeforeClass
	public void beforeClass() {
		setDisposeManagerPerSuite(true);
		setTestTimeoutSecs(240);
	}

	@Before
	public void doSetUp() throws Exception {
		super.doSetUp();
		setDisposeManagerPerSuite(true);
	}

	@Override
	protected String getConfigResources() {
		return "FkIntegrationComponent-common.xml,services/Revoke-fk-service.xml,teststub-services/RevokeMedicalCertificate-fk-teststub-service.xml";
	}

	@Test
	public void testSendMCQMakulering() throws Exception {
		RevokeTransformTestConsumer consumer = new RevokeTransformTestConsumer(
				"http://localhost:11000/tb/eintyg/revoke/SendMedicalCertificateQuestion/1/rivtabp20");

		SendMedicalCertificateQuestionResponseType response = consumer.sendMCQuestion(Amnetyp.MAKULERING_AV_LAKARINTYG);

		Thread.currentThread().sleep(1000);
		
		assertEquals(response.getResult().getResultCode(), ResultCodeEnum.OK);
	}
	
	@Test
	public void testSendMCQOvrigt() throws Exception {
		RevokeTransformTestConsumer consumer = new RevokeTransformTestConsumer(
				"http://localhost:11000/tb/eintyg/revoke/SendMedicalCertificateQuestion/1/rivtabp20");

		SendMedicalCertificateQuestionResponseType response = consumer.sendMCQuestion(Amnetyp.OVRIGT);

		Thread.currentThread().sleep(1000);
		
		assertEquals(response.getResult().getResultCode(), ResultCodeEnum.OK);
	}

}
