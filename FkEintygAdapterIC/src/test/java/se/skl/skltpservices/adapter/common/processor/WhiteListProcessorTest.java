package se.skl.skltpservices.adapter.common.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import org.junit.Test;
import org.mockito.Mockito;
import org.mule.api.transformer.TransformerException;

public class WhiteListProcessorTest {

	@Test
	public void testExtractSenderFromCertificate() throws Exception {

		final X500Principal principal = new X500Principal("OU=marcus");

		final X509Certificate cert = Mockito.mock(X509Certificate.class);
		Mockito.when(cert.getSubjectX500Principal()).thenReturn(principal);

		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.setSenderIdPropertyName("OU");
		processor.setWhiteList("127.0.0.1");

		final String sender = processor.extractSenderIdFromCertificate(cert);

		assertNotNull(sender);
		assertEquals("marcus", sender);

		/*
		 * Verifications
		 */
		Mockito.verify(cert, Mockito.times(1)).getSubjectX500Principal();
	}

	@Test
	public void testExtractSenderFromCertificateInHexMode() throws Exception {
		final String sender = "#131048534153455256494345532d31303358";

		final X500Principal principal = new X500Principal("OU=" + sender);

		final X509Certificate cert = Mockito.mock(X509Certificate.class);
		Mockito.when(cert.getSubjectX500Principal()).thenReturn(principal);

		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.setSenderIdPropertyName("OU");
		processor.setWhiteList("127.0.0.1");

		final String s = processor.extractSenderIdFromCertificate(cert);

		System.out.println("Sender: " + s);

		assertNotNull(s);

	}

	@Test
	public void testExtractSenderWithNullCert() throws Exception {

		final WhiteListProcessor processor = new WhiteListProcessor();

		try {
			processor.extractSenderIdFromCertificate(null);
			fail("Exception not thrown when certificate was null");
		} catch (final IllegalArgumentException e) {
			assertEquals("Cannot extract any sender becuase the pattern used to find it was null", e.getMessage());
			return;
		}

		fail("Expected IllegalArgumentException");
	}

	@Test
	public void testExtractSenderWithNullPattern() throws Exception {
		final WhiteListProcessor processor = new WhiteListProcessor();

		try {
			processor.extractSenderIdFromCertificate(Mockito.mock(X509Certificate.class));
			fail("No exception was thrown when pattern was null");
		} catch (final IllegalArgumentException e) {
			assertEquals("Cannot extract any sender becuase the pattern used to find it was null", e.getMessage());
			return;
		}

		fail("Expected IllegalArgumentException");
	}

	@Test
	public void testNoSenderInCertificate() {
		final X500Principal principal = new X500Principal("CN=marcus");
		final X509Certificate cert = Mockito.mock(X509Certificate.class);
		Mockito.when(cert.getSubjectX500Principal()).thenReturn(principal);

		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.setSenderIdPropertyName("OU");

		try {
			processor.extractSenderIdFromCertificate(cert);
			fail("Did not expect a sender id in certificate");
		} catch (final TransformerException e) {
			assertEquals("No senderId found in Certificate, " + principal.getName(), e.getMessage());
		}

		Mockito.verify(cert, Mockito.times(1)).getSubjectX500Principal();
	}

	@Test
	public void senderIdFoundInWhitelist() throws Exception {
		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.setWhiteList("kalle");
		boolean inWhiteList = processor.senderInWhiteList("kalle");

		assertTrue(inWhiteList);
	}

	@Test
	public void senderIdFoundInListOfManysenderIds() throws Exception {
		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.setWhiteList("Olle,kalle,Pelle");
		boolean inWhiteList = processor.senderInWhiteList("kalle");

		assertTrue(inWhiteList);
	}

	@Test
	public void senderIdNotFoundInListOfManysenderIds() throws Exception {
		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.setWhiteList("Olle,Nisse,Pelle");
		boolean inWhiteList = processor.senderInWhiteList("kalle");

		assertFalse(inWhiteList);
	}

	@Test
	public void senderIdNotFoundInWhitelist() throws Exception {
		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.setWhiteList("Pelle");
		boolean inWhiteList = processor.senderInWhiteList("kalle");

		assertFalse(inWhiteList);
	}

	@Test(expected = TransformerException.class)
	public void whenWhiteListIsEmptyExceptionIsThrown() throws Exception {
		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.senderInWhiteList("kalle");
		fail("Expected TransformerException when no whitelist");
	}

	@Test(expected = TransformerException.class)
	public void whenSenderIdIsEmptyExceptionIsThrown() throws Exception {
		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.setWhiteList("Pelle");
		processor.senderInWhiteList(null);
		fail("Expected TransformerException when no senderId");
	}

	@Test
	public void extractFirstCertificateInChain() throws Exception {

		final X509Certificate firstCertInChain = Mockito.mock(X509Certificate.class);
		final X509Certificate[] certs = new X509Certificate[1];
		certs[0] = firstCertInChain;

		final WhiteListProcessor processor = new WhiteListProcessor();
		X509Certificate firstCertRecieved = processor.extractFirstCertificateInChain(certs);

		assertEquals(firstCertInChain, firstCertRecieved);
	}

	@Test
	public void extractFirstCertificateInChainWhenManyCertsExists() throws Exception {

		final X509Certificate firstCertInChain = Mockito.mock(X509Certificate.class);
		final X509Certificate secondCertInChain = Mockito.mock(X509Certificate.class);
		final X509Certificate thirdCertInChain = Mockito.mock(X509Certificate.class);
		final X509Certificate[] certs = new X509Certificate[3];
		certs[0] = firstCertInChain;
		certs[1] = secondCertInChain;
		certs[2] = thirdCertInChain;

		final WhiteListProcessor processor = new WhiteListProcessor();
		X509Certificate firstCertRecieved = processor.extractFirstCertificateInChain(certs);

		assertEquals(firstCertInChain, firstCertRecieved);
	}

	@Test(expected = TransformerException.class)
	public void whenNoCertificateExistTransformExceptionIsThrown() throws Exception {

		final X509Certificate[] certs = null;

		final WhiteListProcessor processor = new WhiteListProcessor();
		processor.extractFirstCertificateInChain(certs);

		fail("Expected TransformerException when no cert was found in chain");
	}

}
