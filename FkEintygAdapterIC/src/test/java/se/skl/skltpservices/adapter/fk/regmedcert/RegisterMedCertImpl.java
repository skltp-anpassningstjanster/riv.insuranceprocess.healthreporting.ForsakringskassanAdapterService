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
package se.skl.skltpservices.adapter.fk.regmedcert;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.registermedicalcertificate.v3.rivtabp20.RegisterMedicalCertificateResponderInterface;
import se.skl.riv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v3.RegisterMedicalCertificateResponseType;
import se.skl.riv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v3.RegisterMedicalCertificateType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;


@WebService(
		serviceName = "RegisterMedicalCertificateResponderService", 
		endpointInterface="se.skl.riv.insuranceprocess.healthreporting.registermedicalcertificate.v3.rivtabp20.RegisterMedicalCertificateResponderInterface", 
		portName = "RegisterMedicalCertificateResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:RegisterMedicalCertificate:3:rivtabp20")
public class RegisterMedCertImpl implements RegisterMedicalCertificateResponderInterface {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public RegisterMedicalCertificateResponseType registerMedicalCertificate(
			AttributedURIType logicalAddress,
			RegisterMedicalCertificateType parameters) {
		try {
			logger.debug("Received call not validating!");
			RegisterMedicalCertificateResponseType response = new RegisterMedicalCertificateResponseType();
						
			String logiskAdress = logicalAddress.getValue();
			String name = "dummy";
			// Check if we should throw some kind of exception or simulate a timeout.
			if (    parameters != null && parameters.getLakarutlatande() != null && 
					parameters.getLakarutlatande().getPatient() != null && 
					parameters.getLakarutlatande().getPatient().getFullstandigtNamn() != null &&
					parameters.getLakarutlatande().getPatient().getFullstandigtNamn().length() > 0) {

				name = parameters.getLakarutlatande().getPatient().getFullstandigtNamn();
			}
				
			if (name.equalsIgnoreCase("Error") || logiskAdress.contains("Error") ) {
				ResultOfCall result = new ResultOfCall();
				result.setResultCode(ResultCodeEnum.ERROR);
				response.setResult(result);
				logger.debug("Returned Error for not validating!");
			} else if (name.equalsIgnoreCase("Exception") || logiskAdress.contains("Exception")) {
				logger.debug("Returned Exception for not validating!");
				throw new RuntimeException("Exception called");
			} else if (name.equalsIgnoreCase("Timeout") || logiskAdress.contains("Timeout")) {
				logger.debug("Returned Timeout for not validating!");
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
				logger.debug("Returned OK for not validating!");
			}
			
			return response;
		} catch (RuntimeException e) {
			throw e;
		}
	}
}