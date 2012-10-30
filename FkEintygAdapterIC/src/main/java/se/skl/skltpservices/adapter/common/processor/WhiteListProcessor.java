/**
 * Copyright (c) 2012, Sjukvardsradgivningen. All rights reserved.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package se.skl.skltpservices.adapter.common.processor;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageAwareTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteListProcessor extends AbstractMessageAwareTransformer {

	private static final Logger log = LoggerFactory.getLogger(WhiteListProcessor.class);

	private static final String CERT_SENDERID_PATTERN = "=([^,]+)";
	private String whiteList;
	private String senderIdPropertyName;
	private Pattern pattern;
	private String certificatesKey;

	@Override
	public Object transform(MuleMessage muleMessage, String encoding) throws TransformerException {
		final Certificate[] certificateChain = (Certificate[]) muleMessage.getProperty(certificatesKey);

		X509Certificate x509Certificate = extractFirstCertificateInChain(certificateChain);

		String senderId = extractSenderIdFromCertificate(x509Certificate);

		if (!senderInWhiteList(senderId)) {
			throw transformerException("Sender extracted from certificate is not in the whitelist");
		}

		return muleMessage;

	}

	public void setWhiteList(final String whiteList) {
		this.whiteList = whiteList;
	}

	public void setSenderIdPropertyName(String senderIdPropertyName) {
		this.senderIdPropertyName = senderIdPropertyName;
		pattern = Pattern.compile(this.senderIdPropertyName + CERT_SENDERID_PATTERN);
	}

	public void setCertificatesKey(final String certificatesKey) {
		this.certificatesKey = certificatesKey;
	}

	X509Certificate extractFirstCertificateInChain(final Certificate[] certificateChain) throws TransformerException {

		if (certificateChain == null) {
			throw transformerException("No certificate chain was found, can not validate sender");
		}

		X509Certificate x509Certificate = (X509Certificate) certificateChain[0];
		if (x509Certificate == null) {
			throw transformerException("Cannot extract any sender because the certificate was null");
		}
		return x509Certificate;
	}

	boolean senderInWhiteList(String senderId) throws TransformerException {

		log.debug("senderNotInWhiteList({})", senderId);

		if (StringUtils.isBlank(senderId)) {
			throw transformerException("Sender ID could not be found in incoming certificate");
		}

		if (StringUtils.isBlank(whiteList)) {
			throw transformerException("White list does not contain any sender ids to verify agains");
		}

		final String[] whiteListSenderIds = this.whiteList.split(",");
		for (final String s : whiteListSenderIds) {
			if (s.trim().equals(senderId.trim())) {
				log.debug("SenderId found in white list");
				return true;
			}
		}

		log.debug("senderNotInWhiteList({}) returns false, senderId not in whitelist", senderId);
		return false;
	}

	String extractSenderIdFromCertificate(final X509Certificate certificate) throws TransformerException {

		log.debug("Extracting sender id from certificate.");

		if (this.pattern == null) {
			throw new IllegalArgumentException("Cannot extract any sender becuase the pattern used to find it was null");
		}

		final String principalName = certificate.getSubjectX500Principal().getName();
		return extractSenderFromPrincipal(principalName);
	}

	private String extractSenderFromPrincipal(String principalName) throws TransformerException {
		final Matcher matcher = this.pattern.matcher(principalName);

		if (matcher.find()) {
			final String senderId = matcher.group(1);

			log.debug("Found sender id: {}", senderId);
			return senderId.startsWith("#") ? this.convertFromHexToString(senderId.substring(5)) : senderId;
		} else {
			throw transformerException("No senderId found in Certificate, " + principalName);
		}
	}

	private String convertFromHexToString(final String hexString) {
		byte[] txtInByte = new byte[hexString.length() / 2];
		int j = 0;
		for (int i = 0; i < hexString.length(); i += 2) {
			txtInByte[j++] = Byte.parseByte(hexString.substring(i, i + 2), 16);
		}
		return new String(txtInByte);
	}

	private TransformerException transformerException(String msg) throws TransformerException {
		Message errorMsg = MessageFactory.createStaticMessage(msg);
		return new TransformerException(errorMsg);
	}

}
