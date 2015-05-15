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
package se.skl.skltpservices.adapter.fk.sendmedcertanswer;

import javax.jws.WebService;

import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificateanswer.v1.rivtabp20.SendMedicalCertificateAnswerResponderInterface;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.SendMedicalCertificateAnswerResponseType;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.SendMedicalCertificateAnswerType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultOfCall;


@WebService(
		serviceName = "SendMedicalCertificateAnswerResponderService", 
		endpointInterface="se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificateanswer.v1.rivtabp20.SendMedicalCertificateAnswerResponderInterface", 
		portName = "SendMedicalCertificateAnswerResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:SendMedicalCertificateAnswer:1:rivtabp20")
public class SendMedCertAnswerImpl implements SendMedicalCertificateAnswerResponderInterface {

	public SendMedicalCertificateAnswerResponseType sendMedicalCertificateAnswer(
			AttributedURIType logicalAddress,
			SendMedicalCertificateAnswerType parameters) {
		try {
			SendMedicalCertificateAnswerResponseType response = new SendMedicalCertificateAnswerResponseType();
			
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