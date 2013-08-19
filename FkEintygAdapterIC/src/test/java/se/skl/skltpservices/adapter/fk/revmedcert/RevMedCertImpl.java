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
package se.skl.skltpservices.adapter.fk.revmedcert;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.revokemedicalcertificate.v1.rivtabp20.RevokeMedicalCertificateResponderInterface;
import se.skl.riv.insuranceprocess.healthreporting.revokemedicalcertificateresponder.v1.RevokeMedicalCertificateRequestType;
import se.skl.riv.insuranceprocess.healthreporting.revokemedicalcertificateresponder.v1.RevokeMedicalCertificateResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;


@WebService(
		serviceName = "RevokeMedicalCertificateResponderService", 
		endpointInterface="se.skl.riv.insuranceprocess.healthreporting.revokemedicalcertificate.v1.rivtabp20.RevokeMedicalCertificateResponderInterface", 
		portName = "RevokeMedicalCertificateResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:RevokeMedicalCertificate:1:rivtabp20")
public class RevMedCertImpl implements RevokeMedicalCertificateResponderInterface {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	public RevokeMedicalCertificateResponseType revokeMedicalCertificate(
			AttributedURIType logicalAddress,
			RevokeMedicalCertificateRequestType parameters) {
		
		log.info("revokeMedicalCertificate({}, {})", logicalAddress.getValue(), parameters);
		
		try {
			RevokeMedicalCertificateResponseType response = new RevokeMedicalCertificateResponseType();

			ResultOfCall resCall = new ResultOfCall();

			//Send OK
			resCall.setResultCode(ResultCodeEnum.OK);
			response.setResult(resCall);				
			
			log.info("Response returned: {}", resCall.getResultCode());

			return response;
		} catch (RuntimeException e) {
			log.error("Error occured: ", e);
			throw e;
		}
	}
}