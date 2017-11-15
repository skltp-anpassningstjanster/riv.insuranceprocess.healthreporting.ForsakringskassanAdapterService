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

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;
import org.mockito.Mockito;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;

import se.skl.skltpservices.adapter.common.processor.FkAdapterUtil;

public class CertificateExtractorFactoryTest {

	@Test
	public void extractFromHeaderWhenReveresedProxyHeaderExist() throws Exception {

		final MuleMessage msg = Mockito.mock(MuleMessage.class);
		Mockito.when(msg.getProperty(FkAdapterUtil.REVERSE_PROXY_HEADER_NAME, PropertyScope.INBOUND)).thenReturn("ANY VALUE");
		Pattern pattern = null;

		CertificateExtractorFactory factory = new CertificateExtractorFactory(msg, pattern, "127.0.0.1");
		CertificateExtractor certificateExtractor = factory.creaetCertificateExtractor();

		assertTrue(certificateExtractor instanceof CertificateHeaderExtractor);
	}

	@Test
	public void extractFromChainIsDefault() throws Exception {

		final DefaultMuleMessage msg = Mockito.mock(DefaultMuleMessage.class);
		Pattern pattern = null;

		CertificateExtractorFactory factory = new CertificateExtractorFactory(msg, pattern, "127.0.0.1");
		CertificateExtractor certificateExtractor = factory.creaetCertificateExtractor();

		assertTrue(certificateExtractor instanceof CertificateChainExtractor);
	}

}
