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
package se.skl.skltpservices.adapter.fk.sendmedcert;

import javax.jws.WebService;

import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificate.v1.rivtabp20.SendMedicalCertificateResponderInterface;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificateresponder.v1.SendMedicalCertificateRequestType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificateresponder.v1.SendMedicalCertificateResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ErrorIdEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;


@WebService(
		serviceName = "SendMedicalCertificateResponderService", 
		endpointInterface="se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificate.v1.rivtabp20.SendMedicalCertificateResponderInterface", 
		portName = "SendMedicalCertificateResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:SendMedicalCertificate:1:rivtabp20",
		wsdlLocation = "schemas/vard/interactions/SendMedicalCertificateInteraction/SendMedicalCertificateInteraction_1.0_rivtabp20.wsdl")
public class SendMedCertImpl implements SendMedicalCertificateResponderInterface {

	public SendMedicalCertificateResponseType sendMedicalCertificate(
			AttributedURIType logicalAddress,
			SendMedicalCertificateRequestType parameters) {
		try {
			SendMedicalCertificateResponseType response = new SendMedicalCertificateResponseType();
			String logiskAdress = logicalAddress.getValue();
			String name = "dummy";
			// Check if we should throw some kind of exception or simulate a timeout.
			if (    parameters != null && parameters.getSend().getLakarutlatande() != null && 
					parameters.getSend().getLakarutlatande().getPatient() != null && 
					parameters.getSend().getLakarutlatande().getPatient().getFullstandigtNamn() != null &&
					parameters.getSend().getLakarutlatande().getPatient().getFullstandigtNamn().length() > 0) {

				name = parameters.getSend().getLakarutlatande().getPatient().getFullstandigtNamn();
			}
				
			if (name.equalsIgnoreCase("Error") || logiskAdress.contains("Error") ) {
				ResultOfCall result = new ResultOfCall();
				result.setResultCode(ResultCodeEnum.ERROR);
				result.setErrorId(ErrorIdEnum.APPLICATION_ERROR);
				response.setResult(result);
				System.out.println("Returned Error!");
			} else if (name.equalsIgnoreCase("NoCertificate") || logiskAdress.contains("NoCertificate")) {
				ResultOfCall result = new ResultOfCall();
				result.setResultCode(ResultCodeEnum.ERROR);
				result.setErrorId(ErrorIdEnum.APPLICATION_ERROR);
				result.setErrorText("No Medical Certificate found that could be sent");
				response.setResult(result);
				System.out.println("Returned Error for medical certificate not found!");
			} else if (name.equalsIgnoreCase("AlreadySent") || logiskAdress.contains("AlreadySent")) {
				ResultOfCall result = new ResultOfCall();
				result.setResultCode(ResultCodeEnum.INFO);
				result.setInfoText("Medical Certificate already sent");
				response.setResult(result);
				System.out.println("Returned Info for already sent certificate!");
			} else if (name.equalsIgnoreCase("Exception") || logiskAdress.contains("Exception")) {
				System.out.println("Returned Exception for not validating!");
				throw new RuntimeException("Exception called");
			} else if (name.equalsIgnoreCase("Timeout") || logiskAdress.contains("Timeout")) {
				System.out.println("Returned Timeout for not validating!");
				Thread.currentThread();
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				ResultOfCall resCall = new ResultOfCall();
				resCall.setResultCode(ResultCodeEnum.OK);
				response.setResult(resCall);				
				System.out.println("Returned OK for not validating!");
			}

			return response;
		} catch (RuntimeException e) {
			System.out.println("Error occured: " + e);
			throw e;
		}
	}
}