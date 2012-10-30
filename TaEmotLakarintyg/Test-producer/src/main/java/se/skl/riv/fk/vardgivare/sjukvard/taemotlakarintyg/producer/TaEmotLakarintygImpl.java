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
package se.skl.riv.fk.vardgivare.sjukvard.taemotlakarintyg.producer;

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
			TaEmotLakarintygType parameters) {

		logger.info("taEmotLakarintyg({}, {})", logicalAddress.getValue(),
				parameters);

		try {
			TaEmotLakarintygResponseType response = new TaEmotLakarintygResponseType();
			logger.info("response sent!");
			return response;
		} catch (RuntimeException e) {
			throw new RuntimeException("Error occured in taEmotLakarintyg", e);
		}
	}

}