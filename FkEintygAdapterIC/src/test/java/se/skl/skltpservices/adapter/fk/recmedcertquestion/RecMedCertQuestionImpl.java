/**
 * Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
 */
package se.skl.skltpservices.adapter.fk.recmedcertquestion;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestion.v1.rivtabp20.ReceiveMedicalCertificateQuestionResponderInterface;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestionresponder.v1.ReceiveMedicalCertificateQuestionResponseType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestionresponder.v1.ReceiveMedicalCertificateQuestionType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;


@WebService(
		serviceName = "ReceiveMedicalCertificateQuestionResponderService", 
		endpointInterface="se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestion.v1.rivtabp20.ReceiveMedicalCertificateQuestionResponderInterface", 
		portName = "ReceiveMedicalCertificateQuestionResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:ReceiveMedicalCertificateQuestion:1:rivtabp20",
		wsdlLocation = "schemas/vard/interactions/ReceiveMedicalCertificateQuestionInteraction/ReceiveMedicalCertificateQuestionInteraction_1.0_rivtabp20.wsdl")
public class RecMedCertQuestionImpl implements ReceiveMedicalCertificateQuestionResponderInterface {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	public ReceiveMedicalCertificateQuestionResponseType receiveMedicalCertificateQuestion(
			AttributedURIType logicalAddress,
			ReceiveMedicalCertificateQuestionType parameters) {
		
		log.info("receiveMedicalCertificateQuestion({}, {})", logicalAddress.getValue(), parameters);
		
		try {
			ReceiveMedicalCertificateQuestionResponseType response = new ReceiveMedicalCertificateQuestionResponseType();

			ResultOfCall resCall = new ResultOfCall();

			// Check if to send an ERROR
			if (parameters.getQuestion().getLakarutlatande().getPatient().getPersonId().getExtension().equalsIgnoreCase("19101010-1234")) {
				resCall.setResultCode(ResultCodeEnum.ERROR);
				response.setResult(resCall);				
			} else {
				resCall.setResultCode(ResultCodeEnum.OK);
				response.setResult(resCall);				
			}
			
			log.info("Response returned: {}", resCall.getResultCode());

			return response;
		} catch (RuntimeException e) {
			log.error("Error occured: ", e);
			throw e;
		}
	}
}