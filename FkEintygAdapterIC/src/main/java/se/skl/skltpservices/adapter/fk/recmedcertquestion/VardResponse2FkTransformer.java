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
package se.skl.skltpservices.adapter.fk.recmedcertquestion;

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.api.routing.ResponseTimeoutException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaResponseType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestionresponder.v1.ReceiveMedicalCertificateQuestionResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.skltpservices.adapter.common.processor.FkAdapterUtil;

public class VardResponse2FkTransformer extends AbstractMessageTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final JaxbUtil JAXB_UTIL = new JaxbUtil(TaEmotFragaResponseType.class);

	public VardResponse2FkTransformer() {
		super();
		registerSourceType(DataTypeFactory.create(Object.class));
	}

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		logger.info("Entering vard2fk receive medical certificate question transform");

		boolean faultDetected = false;

		Object src = message.getPayload();
		try {
			// Take care of any error message and send it back as a SOAP Fault!
			if (src instanceof NullPayload) {
				// We got a null-payload, let's see if there is an
				// exception-payload instead...
				ExceptionPayload ep = message.getExceptionPayload();
				if (ep != null) {
					String errorMessage = ep.getCode() + ": " + ep.getMessage();

					Throwable t = ep.getException();
					if (t instanceof ResponseTimeoutException) {
						src = errorMessage + ". Unknown error.";
					} else {
						src = errorMessage + ". Timeout";
					}

					// Remove the ExceptionPayload!
					message.setExceptionPayload(null);
				}
				faultDetected = true;
			} else if (!(src instanceof ReceiveMedicalCertificateQuestionResponseType)) {
				src = "Payload type not supported: " + message.getPayload().getClass();
				faultDetected = true;
			}

			StringBuffer result = new StringBuffer();

			// First create the content in the body, either a fault or the
			// response
			if (faultDetected) {
				// Strip off xml processing instructions if any
				String payload = (String) src;
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}

				result.append(FkAdapterUtil.generateErrorCallingServiceProducerSoapFaultWithCause(payload));
			} else {
				ReceiveMedicalCertificateQuestionResponseType inResponse = (ReceiveMedicalCertificateQuestionResponseType) src;

				// Create new JAXB object for the outgoing data
				TaEmotFragaResponseType outResponse = new TaEmotFragaResponseType();

				// Handle error if ResultCode == ERROR!
				if (inResponse.getResult().getResultCode().equals(ResultCodeEnum.ERROR)) {
					// Get Error text if any
					String errorText = "";
					if (inResponse.getResult().getErrorText() != null
							&& inResponse.getResult().getErrorText().length() > 0) {
						errorText = inResponse.getResult().getErrorText();
					}
					result.append(FkAdapterUtil.generateErrorCallingServiceProducerSoapFaultWithCause(errorText));
					logger.debug("Return SOAP Envelope: {}", result.toString());
					return result.toString();
				}

				// Transform the JAXB object into a XML payload		
				String payload = JAXB_UTIL.marshal(outResponse, "urn:riv:fk:vardgivare:sjukvard:TaEmotFraga:1:rivtabp20", "TaEmotFragaResponse");
				
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}
				result.append(payload);
			}

			// Done, return the string
			String resultStr = result.toString();
			logger.debug("Return SOAP Envelope: {}", resultStr);

			logger.info("Exiting vard2fk receive medical certificate question transform");

			return resultStr;

		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
	}
}