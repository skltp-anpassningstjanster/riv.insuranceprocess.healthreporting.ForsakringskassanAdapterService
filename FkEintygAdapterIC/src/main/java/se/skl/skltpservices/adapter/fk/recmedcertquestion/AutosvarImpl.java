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

/**
 * Validation class that will certify a webservice call made for a question regarding a medical certificate.. We will check mandatory/optional fields and all other declared rules.
 * @author matsek
 *
 */

@WebService(
		serviceName = "ReceiveMedicalCertificateQuestionResponderService", 
		endpointInterface="se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestion.v1.rivtabp20.ReceiveMedicalCertificateQuestionResponderInterface", 
		portName = "ReceiveMedicalCertificateQuestionResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:ReceiveMedicalCertificateQuestion:1:rivtabp20",
		wsdlLocation = "schemas/vard/interactions/ReceiveMedicalCertificateQuestionInteraction/ReceiveMedicalCertificateQuestionInteraction_1.0_rivtabp20.wsdl")
public class AutosvarImpl implements ReceiveMedicalCertificateQuestionResponderInterface {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ReceiveMedicalCertificateQuestionResponseType receiveMedicalCertificateQuestion(
			AttributedURIType logicalAddress,
			ReceiveMedicalCertificateQuestionType parameters) {
		
		// Create a response and set result of validation            
		ReceiveMedicalCertificateQuestionResponseType outResponse = new ReceiveMedicalCertificateQuestionResponseType();
		ResultOfCall outResCall = new ResultOfCall();
		outResponse.setResult(outResCall);

		try {
			// Send a new Answer before answering to this request
			AutosvarAnswer autoanswer = new AutosvarAnswer(parameters.getQuestion());
			autoanswer.start();
					
			// Return OK!            
			outResCall.setResultCode(ResultCodeEnum.OK);
			outResponse.setResult(outResCall);
			
			return outResponse;
		} catch (Exception e) {
			outResCall.setErrorText(e.getMessage());
			outResCall.setResultCode(ResultCodeEnum.ERROR);
			logger.error("Autosvar exception: " + e.getMessage());
			return outResponse;
		}
	}
}