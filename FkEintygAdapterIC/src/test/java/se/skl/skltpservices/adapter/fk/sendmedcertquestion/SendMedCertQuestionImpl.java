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
package se.skl.skltpservices.adapter.fk.sendmedcertquestion;

import javax.jws.WebService;

import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestion.v1.rivtabp20.SendMedicalCertificateQuestionResponderInterface;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionResponseType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;


@WebService(
		serviceName = "SendMedicalCertificateQuestionResponderService", 
		endpointInterface="se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestion.v1.rivtabp20.SendMedicalCertificateQuestionResponderInterface", 
		portName = "SendMedicalCertificateQuestionResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:SendMedicalCertificateQuestion:1:rivtabp20",
		wsdlLocation = "schemas/vard/interactions/SendMedicalCertificateQuestionInteraction/SendMedicalCertificateQuestionInteraction_1.0_rivtabp20.wsdl")
public class SendMedCertQuestionImpl implements SendMedicalCertificateQuestionResponderInterface {

	public SendMedicalCertificateQuestionResponseType sendMedicalCertificateQuestion(
			AttributedURIType logicalAddress,
			SendMedicalCertificateQuestionType parameters) {
		try {
			SendMedicalCertificateQuestionResponseType response = new SendMedicalCertificateQuestionResponseType();
			
			// Ping response
			ResultOfCall resCall = new ResultOfCall();
			resCall.setResultCode(ResultCodeEnum.OK);
			response.setResult(resCall);

			return response;
		} catch (RuntimeException e) {
			System.out.println("Error occured: " + e);
			throw e;
		}
	}
}