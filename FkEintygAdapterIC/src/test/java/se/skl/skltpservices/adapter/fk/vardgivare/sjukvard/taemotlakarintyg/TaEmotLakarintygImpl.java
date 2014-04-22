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
package se.skl.skltpservices.adapter.fk.vardgivare.sjukvard.taemotlakarintyg;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.fk.vardgivare.sjukvard.taemotlakarintyg.v1.rivtabp20.TaEmotLakarintygResponderInterface;
import se.fk.vardgivare.sjukvard.taemotlakarintygresponder.v1.TaEmotLakarintygResponseType;
import se.fk.vardgivare.sjukvard.taemotlakarintygresponder.v1.TaEmotLakarintygType;

@WebService(serviceName = "TaEmotLakarintygResponderService", endpointInterface = "se.fk.vardgivare.sjukvard.taemotlakarintyg.v1.rivtabp20.TaEmotLakarintygResponderInterface", portName = "TaEmotLakarintygResponderPort", targetNamespace = "urn:riv:fk:vardgivare:sjukvard:TaEmotLakarintyg:1:rivtabp20", wsdlLocation = "schemas/fk/TaEmotLakarintygInteraction_1.0_rivtabp20.wsdl")
public class TaEmotLakarintygImpl implements TaEmotLakarintygResponderInterface {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public TaEmotLakarintygResponseType taEmotLakarintyg(
			org.w3.wsaddressing10.AttributedURIType logicalAddress,
			TaEmotLakarintygType request) {

		logger.info("taEmotLakarintyg({}, {})", logicalAddress.getValue(), request);
		
		String ssn = request.getFKSKLTaEmotLakarintygAnrop().getPatient().getIdentifierare();
		
		logger.debug("Patient with ssn {} was requested", ssn);
		
		if("19721212-1212".equals(ssn)){
			logger.debug("Patient ssn {} triggers an exception to be returned from testproducer", ssn);
			throw new RuntimeException("Exception triggered by sending in patient " + ssn);
		}

		try {
			TaEmotLakarintygResponseType response = new TaEmotLakarintygResponseType();
			logger.info("Response sent for patient with ssn {}", ssn);
			return response;
		} catch (RuntimeException e) {
			throw new RuntimeException("Error occured in taEmotLakarintyg", e);
		}
	}

}