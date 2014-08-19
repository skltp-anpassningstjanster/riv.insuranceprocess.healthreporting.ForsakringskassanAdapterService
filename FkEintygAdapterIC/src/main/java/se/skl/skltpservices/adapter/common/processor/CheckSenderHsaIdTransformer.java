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
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckSenderHsaIdTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(CheckSenderHsaIdTransformer.class);

	private static final String CERT_SENDERID_PATTERN = "=([^,]+)";
	private String whiteList;
	private String senderIdPropertyName;
	private Pattern pattern;
	private String certificatesKey;
	
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

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		final Certificate[] certificateChain = (Certificate[]) message.getProperty(certificatesKey);

		X509Certificate x509Certificate = extractFirstCertificateInChain(certificateChain);

		String senderId = extractSenderIdFromCertificate(x509Certificate);
		
		log.debug("Extracted sender HSA ID {}, check if its valid against whitelist", senderId);

		if (!isCallerOnWhiteList(senderId, whiteList)) {
			log.error("Not a valid HSA ID! Sender HSA ID {} was not found in whitelist", whiteList);
			throw transformerException("FKADAPT003 Caller was not on the white list of accepted HSA ID's. HSA ID: " + senderId);
		}

		return message;
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

	private TransformerException transformerException(String msg) {
		Message errorMsg = MessageFactory.createStaticMessage(msg);
		return new TransformerException(errorMsg);
	}
	
	/*
	 * Check if the entry provided by the caller is on accepted list of entries in whitelist. False
	 * is always returned in case no whitelist exist or provided call entry is empty.
	 * 
	 * @param callerEntry The callers entry to match againts whitelist entries
	 * @param whiteList The comma separated whitelist of entries to compare againts 
	 * @return true if caller entry is on whitelist
	 */
	static boolean isCallerOnWhiteList(String callerEntry, String whiteList) {
		
		log.debug("Check if caller {} is in whitelist...", callerEntry);

		if (StringUtils.isBlank(callerEntry)) {
			log.warn("A potential empty ip address from the caller");
			return false;
		}
		
		if (StringUtils.isBlank(whiteList)) {
			log.error("An empty whitelist is used when checking if caller is on whitelist, not ok. Check will return false!");
			return false;
		}
		
		for (String whiteListEntry : whiteList.split(",")) {
			if(callerEntry.equals(whiteListEntry.trim())){
				log.debug("Caller matches entry in white list, ok");
				return true;
			}
		}

		log.warn("Caller was not on the white list of accepted entries. Caller entry: {}, accepted entries in whitelist: {}", callerEntry, whiteList);
		return false;
	}

}
