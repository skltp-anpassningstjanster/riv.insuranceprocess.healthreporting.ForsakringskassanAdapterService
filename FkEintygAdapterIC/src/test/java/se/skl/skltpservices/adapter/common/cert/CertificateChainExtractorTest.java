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
package se.skl.skltpservices.adapter.common.cert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

import javax.security.auth.x500.X500Principal;

import org.junit.Test;
import org.mockito.Mockito;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.skl.skltpservices.adapter.common.processor.FkAdapterUtil;

public class CertificateChainExtractorTest {

	private Pattern pattern = Pattern.compile("OU" + FkAdapterUtil.CERT_SENDERID_PATTERN);
	
	RecursiveResourceBundle rrb = new RecursiveResourceBundle("FkIntegrationComponent-config", "FkIntegrationComponent-config-override");
	
	String certificatesKey = rrb.getString("CERTIFICATE_KEY");

	/**
	 * Test that we can extract a certificate that is in the mule message.
	 * Regular mode.
	 */
	@Test
	public void testExtractX509CertificateCertificateFromChain() {

		final MuleMessage msg = mockCert();

		final CertificateChainExtractor helper = new CertificateChainExtractor(msg, pattern, "HSAID1");
		final String senderId = helper.extractSenderIdFromCertificate(certificatesKey);

		Mockito.verify(msg, Mockito.times(0)).getProperty(FkAdapterUtil.REVERSE_PROXY_HEADER_NAME, PropertyScope.INBOUND);
		Mockito.verify(msg, Mockito.times(1)).getProperty(certificatesKey, PropertyScope.OUTBOUND);

		assertNotNull(senderId);
		assertEquals("Harmony", senderId);
	}

	@Test
	public void testExtractCertificateWhenChainIsNull() throws Exception {

		final MuleMessage msg = Mockito.mock(MuleMessage.class);

		final CertificateChainExtractor helper = new CertificateChainExtractor(msg, null, "127.0.0.1");
		try {
			helper.extractSenderIdFromCertificate(certificatesKey);
			fail("Exception was not thrown when certificate chain was null");
		} catch (Exception e) {
			assertEquals("No certificate was found in request, therefore sender is not trusted.", e.getMessage());
		}

		Mockito.verify(msg, Mockito.times(0)).getProperty(FkAdapterUtil.REVERSE_PROXY_HEADER_NAME, PropertyScope.INBOUND);
		Mockito.verify(msg, Mockito.times(1)).getProperty(certificatesKey, PropertyScope.OUTBOUND);
	}

	@Test
	public void testExtractNoX509CertificateFromChain() throws Exception {

		final Certificate cert = Mockito.mock(Certificate.class);
		final Certificate[] certs = new Certificate[1];
		certs[0] = cert;

		final MuleMessage msg = Mockito.mock(MuleMessage.class);
		Mockito.when(msg.getProperty(certificatesKey, PropertyScope.OUTBOUND)).thenReturn(certs);

		final CertificateChainExtractor helper = new CertificateChainExtractor(msg, null, "HSAID1");

		try {
			helper.extractSenderIdFromCertificate(certificatesKey);
			fail("No exception was thrown when certificate in cert chain was of wrong type");
		} catch (Exception e) {
			assertEquals("Unkown certificate type found in https request, can not validate sender", e.getMessage());
		}

		Mockito.verify(msg, Mockito.times(0)).getProperty(FkAdapterUtil.REVERSE_PROXY_HEADER_NAME, PropertyScope.INBOUND);
		Mockito.verify(msg, Mockito.times(1)).getProperty(certificatesKey, PropertyScope.OUTBOUND);
	}

	private MuleMessage mockCert() {

		X500Principal principal = new X500Principal(
				"CN=Hermione Granger, O=Apache Software Foundation, OU=Harmony, L=Hogwarts, ST=Hants, C=GB");

		final X509Certificate cert = Mockito.mock(X509Certificate.class);
		Mockito.when(cert.getSubjectX500Principal()).thenReturn(principal);

		final Certificate[] certs = new Certificate[1];
		certs[0] = cert;

		final DefaultMuleMessage msg = Mockito.mock(DefaultMuleMessage.class);
		Mockito.when(msg.getProperty(certificatesKey, PropertyScope.OUTBOUND)).thenReturn(certs);

		return msg;
	}

}
