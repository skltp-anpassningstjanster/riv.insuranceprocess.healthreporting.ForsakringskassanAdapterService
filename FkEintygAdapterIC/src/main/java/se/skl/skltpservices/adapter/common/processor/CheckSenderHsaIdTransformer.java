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
import org.mule.api.transport.PropertyScope;
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
		
		String senderId = message.getProperty(FkAdapterUtil.X_FK_SENDER_ID, PropertyScope.INBOUND, null);
		
		log.debug("Is property {} found in inbound properties?", FkAdapterUtil.X_FK_SENDER_ID);
		
		/*
		 * Sender id was not found in http header x-fk-sender-id, fall back to the certificate control.
		 */
		if(StringUtils.isBlank(senderId)){
			log.debug("No, look into the senders certificate instead");
			final Certificate[] certificateChain = (Certificate[]) message.getProperty(certificatesKey);
			X509Certificate x509Certificate = extractFirstCertificateInChain(certificateChain);
			senderId = extractSenderIdFromCertificate(x509Certificate);
		}
		
		log.debug("Sender id found in request {}, check against whitelist!", senderId);
		if (!isCallerOnWhiteList(senderId, whiteList, FkAdapterUtil.X_FK_SENDER_ID)) {
			throw transformerException("FKADAPT003 Caller was not on the white list of accepted HSA ID's. HSA ID: " + senderId);
		}
		
		message.setProperty(FkAdapterUtil.FK_SENDER_ID, senderId, PropertyScope.OUTBOUND);

		return message;
	}

	X509Certificate extractFirstCertificateInChain(final Certificate[] certificateChain) throws TransformerException {

		if (certificateChain == null) {
			throw transformerException("No HTTP property " +FkAdapterUtil.X_FK_SENDER_ID+ ", nor certificate chain was found in request, can not validate sender");
		}

		X509Certificate x509Certificate = (X509Certificate) certificateChain[0];
		if (x509Certificate == null) {
			throw transformerException("No HTTP property " +FkAdapterUtil.X_FK_SENDER_ID+ ", nor certificate was found in request, can not validate sender");
		}
		return x509Certificate;
	}

	String extractSenderIdFromCertificate(final X509Certificate certificate) throws TransformerException {

		log.debug("Extracting sender id from certificate.");

		if (this.pattern == null) {
			throw new IllegalArgumentException("Cannot extract any sender from certificate, configured pattern is null. PLease update adapter property FK_CERT_SENDERID");
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
	 * @param httpHeader The http header causing the check in the white list
	 * @return true if caller entry is on whitelist
	 */
	static boolean isCallerOnWhiteList(String callerHsaId, String whiteList, String httpHeader) {
		
		log.debug("Check if caller {} is in white list berfore using HTTP header {}...", callerHsaId, httpHeader);

		if (StringUtils.isBlank(callerHsaId)) {
			log.warn("A potential empty HSA ID from the caller, HSA ID is: {}. HTTP header that caused checking: {} ", callerHsaId, httpHeader);
			return false;
		}
		
		if (StringUtils.isBlank(whiteList)) {
			log.warn("A check against the HSA ID whitelist was requested, but the whitelist is configured empty. Update adapter configuration property FK_WHITE_LIST");
			return false;
		}
		
		for (String whiteListEntry : whiteList.split(",")) {
			if(callerHsaId.equals(whiteListEntry.trim())){
				log.debug("Caller matches entry in white list, ok");
				return true;
			}
		}

		log.error("Not a valid HSA ID! Sender HSA ID {} was not found in whitelist {}", callerHsaId, whiteList);
		return false;
	}

}
