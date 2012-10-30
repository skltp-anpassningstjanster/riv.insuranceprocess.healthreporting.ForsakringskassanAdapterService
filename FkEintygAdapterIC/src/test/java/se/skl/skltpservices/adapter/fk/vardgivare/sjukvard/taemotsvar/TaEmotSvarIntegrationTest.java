package se.skl.skltpservices.adapter.fk.vardgivare.sjukvard.taemotsvar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.soitoolkit.commons.mule.test.AbstractTestCase;

import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarResponseType;

public class TaEmotSvarIntegrationTest extends AbstractTestCase {

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
		return "FkIntegrationComponent-common.xml,services/ReceiveMedicalertificateAnswer-fk-service.xml,teststub-services/ReceiveMedicalertificateAnswer-fk-teststub-service.xml";
	}

	@Test
	public void testTaEmotSvar() throws Exception {

		TaEmotSvarTestConsumer consumer = new TaEmotSvarTestConsumer(
				"https://localhost:12000/tb/fk/ifv/TaEmotSvar/1/rivtabp20");

		TaEmotSvarResponseType response = consumer.taEmotSvar();

		assertNotNull(response);

	}

}
