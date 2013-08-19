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
package se.skl.skltpservices.adapter.fk.sendmedcertquestion;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestion.v1.rivtabp20.SendMedicalCertificateQuestionResponderInterface;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionResponseType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;

/**
 * Validation class that will certify a webservice call made for a question regarding a medical certificate.. We will check mandatory/optional fields and all other declared rules.
 * @author matsek
 *
 */

@WebService(
		serviceName = "SendMedicalCertificateQuestionResponderService", 
		endpointInterface="se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestion.v1.rivtabp20.SendMedicalCertificateQuestionResponderInterface", 
		portName = "SendMedicalCertificateQuestionResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:SendMedicalCertificateQuestion:1:rivtabp20",
		wsdlLocation = "schemas/vard/interactions/SendMedicalCertificateQuestionInteraction/SendMedicalCertificateQuestionInteraction_1.0_rivtabp20.wsdl")
public class SendMCQRevoke implements SendMedicalCertificateQuestionResponderInterface {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SendMedicalCertificateQuestionResponseType sendMedicalCertificateQuestion(
			AttributedURIType logicalAddress,
			SendMedicalCertificateQuestionType parameters) {
		
		// Log incoming call
		if (parameters.getQuestion() != null && parameters.getQuestion().getAmne() != null && 
			parameters.getQuestion().getLakarutlatande() != null && parameters.getQuestion().getLakarutlatande().getLakarutlatandeId() != null)
		{
			logger.info("Received SendMedicalCerticateQuestion. Subject = " + parameters.getQuestion().getAmne().toString() + ", Certificate ID = " + parameters.getQuestion().getLakarutlatande().getLakarutlatandeId() + ".");
		}
		
		// Create a response and set result of validation            
		SendMedicalCertificateQuestionResponseType outResponse = new SendMedicalCertificateQuestionResponseType();
		ResultOfCall outResCall = new ResultOfCall();
		outResponse.setResult(outResCall);

		try {
			// Check if this question has subject makulering 
			if (parameters.getQuestion() != null && 
				parameters.getQuestion().getAmne() != null &&
				parameters.getQuestion().getAmne().equals(Amnetyp.MAKULERING_AV_LAKARINTYG)) {

				// Send a revokequestion
				RevokeCertificate wiretapQuestion = new RevokeCertificate(parameters.getQuestion());
				wiretapQuestion.start();
			}
			// Return OK!            
			outResCall.setResultCode(ResultCodeEnum.OK);
			outResponse.setResult(outResCall);
			
			return outResponse;
		} catch (Exception e) {
			outResCall.setErrorText(e.getMessage());
			outResCall.setResultCode(ResultCodeEnum.ERROR);
			logger.error("RevokeTransform exception: " + e.getMessage());
			return outResponse;
		}
	}
}