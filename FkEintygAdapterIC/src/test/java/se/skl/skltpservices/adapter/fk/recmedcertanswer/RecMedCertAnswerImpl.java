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
package se.skl.skltpservices.adapter.fk.recmedcertanswer;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.insuranceprocess.healthreporting.receivemedicalcertificateanswer.v1.rivtabp20.ReceiveMedicalCertificateAnswerResponderInterface;
import se.inera.ifv.insuranceprocess.healthreporting.receivemedicalcertificateanswerresponder.v1.ReceiveMedicalCertificateAnswerResponseType;
import se.inera.ifv.insuranceprocess.healthreporting.receivemedicalcertificateanswerresponder.v1.ReceiveMedicalCertificateAnswerType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ErrorIdEnum;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultOfCall;

@WebService(serviceName = "ReceiveMedicalCertificateAnswerResponderService", endpointInterface = "se.inera.ifv.insuranceprocess.healthreporting.receivemedicalcertificateanswer.v1.rivtabp20.ReceiveMedicalCertificateAnswerResponderInterface", portName = "ReceiveMedicalCertificateAnswerResponderPort", targetNamespace = "urn:riv:insuranceprocess:healthreporting:ReceiveMedicalCertificateAnswer:1:rivtabp20")
public class RecMedCertAnswerImpl implements ReceiveMedicalCertificateAnswerResponderInterface {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public ReceiveMedicalCertificateAnswerResponseType receiveMedicalCertificateAnswer(
			AttributedURIType logicalAddress, ReceiveMedicalCertificateAnswerType parameters) {

		log.info("receiveMedicalCertificateAnswer({},{})", logicalAddress.getValue(), parameters.getAnswer().getSvar()
				.getMeddelandeText());

		try {
			ReceiveMedicalCertificateAnswerResponseType response = new ReceiveMedicalCertificateAnswerResponseType();

			String personId = parameters.getAnswer().getLakarutlatande().getPatient().getPersonId().getExtension();

			log.info("Received personId: {}", personId);

			// Check if to send an ERROR
			if (parameters.getAnswer().getLakarutlatande().getPatient().getPersonId().getExtension()
					.equalsIgnoreCase("19101010-1234")) {
				response.setResult(createErrorResultOfCall());
			} else {
				response.setResult(createOKResultOfCall());
			}

			log.info("Response returned: {} {}", response.getResult().getResultCode());

			return response;
		} catch (RuntimeException e) {
			log.error("Error occured: " + e);
			throw e;
		}
	}

	private ResultOfCall createErrorResultOfCall() {
		ResultOfCall resultOfCall = new ResultOfCall();
		resultOfCall.setResultCode(ResultCodeEnum.ERROR);
		resultOfCall.setErrorId(ErrorIdEnum.VALIDATION_ERROR);
		resultOfCall.setErrorText("It went wrong");
		return resultOfCall;
	}

	private ResultOfCall createOKResultOfCall() {
		ResultOfCall resultOfCall = new ResultOfCall();
		resultOfCall.setInfoText("It went ok");
		resultOfCall.setResultCode(ResultCodeEnum.OK);
		return resultOfCall;
	}
}