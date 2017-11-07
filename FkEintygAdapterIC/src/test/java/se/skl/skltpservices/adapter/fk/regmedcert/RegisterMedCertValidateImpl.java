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
package se.skl.skltpservices.adapter.fk.regmedcert;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.insuranceprocess.healthreporting.registermedicalcertificate.v3.rivtabp20.RegisterMedicalCertificateResponderInterface;
import se.inera.ifv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v3.RegisterMedicalCertificateResponseType;
import se.inera.ifv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v3.RegisterMedicalCertificateType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultOfCall;

/**
 * Validation class that will certify a webservice call made for MU7263. We will check mandatory/optional fields and all other declared rules.
 * @author matsek
 *
 */

@WebService(
		serviceName = "RegisterMedicalCertificateResponderService", 
		endpointInterface="se.inera.ifv.insuranceprocess.healthreporting.registermedicalcertificate.v3.rivtabp20.RegisterMedicalCertificateResponderInterface", 
		portName = "RegisterMedicalCertificateResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:RegisterMedicalCertificate:3:rivtabp20")
public class RegisterMedCertValidateImpl implements RegisterMedicalCertificateResponderInterface {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private Vard2FkValidator validator = new Vard2FkValidator();

	
	public RegisterMedicalCertificateResponseType registerMedicalCertificate( AttributedURIType logicalAddress, RegisterMedicalCertificateType parameters) {
		logger.debug("Received call to validating");

		// Create a response and set result of validation            
		RegisterMedicalCertificateResponseType outResponse = new RegisterMedicalCertificateResponseType();
		ResultOfCall outResCall = new ResultOfCall();
		outResponse.setResult(outResCall);

		try {

            // Validate incoming request
            validator.validateRequest(parameters);
       
			// No validation errors! Return OK!            
			outResCall.setResultCode(ResultCodeEnum.OK);
			logger.debug("Returned OK for validating!");

			return outResponse;
		} catch (Exception e) {
			logger.debug("Exception for validating! Errors = " + e.getMessage());
			outResCall.setErrorText(e.getMessage());
			outResCall.setResultCode(ResultCodeEnum.ERROR);
			return outResponse;
		}
	}
			
}
