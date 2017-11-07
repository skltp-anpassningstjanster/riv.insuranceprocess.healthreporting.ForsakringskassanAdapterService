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

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extractor used when extracting certificate from the certificate chain.
 * 
 */
public class CertificateChainExtractor extends CertificateExtractorBase implements CertificateExtractor {

	private static Logger log = LoggerFactory.getLogger(CertificateChainExtractor.class);

	public CertificateChainExtractor(MuleMessage muleMessage, Pattern pattern, String whiteList) {
		super(muleMessage, pattern, whiteList);
	}

	@Override
	public String extractSenderIdFromCertificate(String certificatesKey) {
		log.debug("Extracting X509Certificate senderId from chain");
		final Certificate[] certificateChain = (Certificate[]) this.getMuleMessage().getProperty(certificatesKey, PropertyScope.OUTBOUND);
		X509Certificate certificate = extraxtCertFromChain(certificateChain);
		return extractSenderIdFromCertificate(certificate);
	}
	
	X509Certificate extraxtCertFromChain(final Certificate[] certificateChain) {

		if (certificateChain == null) {
			throw new RuntimeException("No certificate was found in request, therefore sender is not trusted.");
		}
		try {
			return (X509Certificate) certificateChain[0];
		} catch (Exception e) {
			log.error("Error occured trying to extract certificate from chain, is it a valid X509Certificate in request", e);
			throw new RuntimeException("Unkown certificate type found in https request, can not validate sender");
		}
	}
}
