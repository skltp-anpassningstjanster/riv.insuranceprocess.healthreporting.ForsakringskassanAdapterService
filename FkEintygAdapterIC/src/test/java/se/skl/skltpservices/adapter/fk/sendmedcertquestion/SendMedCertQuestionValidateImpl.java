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
package se.skl.skltpservices.adapter.fk.sendmedcertquestion;

import javax.jws.WebService;

import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificatequestion.v1.rivtabp20.SendMedicalCertificateQuestionResponderInterface;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionResponseType;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultOfCall;

/**
 * Validation class that will certify a webservice call made for a question regarding a medical certificate.. We will check mandatory/optional fields and all other declared rules.
 * @author matsek
 *
 */

@WebService(
		serviceName = "SendMedicalCertificateQuestionResponderService", 
		endpointInterface="se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificatequestion.v1.rivtabp20.SendMedicalCertificateQuestionResponderInterface", 
		portName = "SendMedicalCertificateQuestionResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:SendMedicalCertificateQuestion:1:rivtabp20")
public class SendMedCertQuestionValidateImpl implements SendMedicalCertificateQuestionResponderInterface {

    private VardRequest2FkValidator validator = new VardRequest2FkValidator();

	public SendMedicalCertificateQuestionResponseType sendMedicalCertificateQuestion(
			AttributedURIType logicalAddress,
			SendMedicalCertificateQuestionType parameters) {
		
		// Create a response and set result of validation            
		SendMedicalCertificateQuestionResponseType outResponse = new SendMedicalCertificateQuestionResponseType();
		ResultOfCall outResCall = new ResultOfCall();
		outResponse.setResult(outResCall);

		try {
	        // Validate incoming request
			validator.validateRequest(parameters);
			// No validation errors! Return OK!            
			outResCall.setResultCode(ResultCodeEnum.OK);
			return outResponse;
		} catch (Exception e) {
			outResCall.setErrorText(e.getMessage());
			outResCall.setResultCode(ResultCodeEnum.ERROR);
			return outResponse;
		}
	}
	
}