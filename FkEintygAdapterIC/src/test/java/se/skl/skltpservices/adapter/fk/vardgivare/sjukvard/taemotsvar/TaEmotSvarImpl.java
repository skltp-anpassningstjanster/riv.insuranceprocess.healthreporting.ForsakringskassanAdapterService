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
package se.skl.skltpservices.adapter.fk.vardgivare.sjukvard.taemotsvar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.w3.wsaddressing10.AttributedURIType;

import se.fk.vardgivare.sjukvard.taemotsvar.v1.rivtabp20.TaEmotSvarResponderInterface;
import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarResponseType;
import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarType;

@WebService(
		serviceName = "TaEmotSvarResponderService", 
		endpointInterface="se.fk.vardgivare.sjukvard.taemotsvar.v1.rivtabp20.TaEmotSvarResponderInterface", 
		portName = "TaEmotSvarResponderPort", 
		targetNamespace = "urn:riv:fk:vardgivare:sjukvard:TaEmotSvar:1:rivtabp20",
		wsdlLocation = "schemas/fk/TaEmotSvarInteraction_1.0_rivtabp20.wsdl")
public class TaEmotSvarImpl implements TaEmotSvarResponderInterface {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final JaxbUtil JAXB_UTIL = new JaxbUtil(TaEmotSvarResponseType.class);

	static Map<String, List<String>> answerMapFK = new HashMap<String, List<String>>(); 
	static Map<String, List<String>> answerMapVard = new HashMap<String, List<String>>(); 
	

	public TaEmotSvarResponseType taEmotSvar(
			AttributedURIType logicalAddress, TaEmotSvarType parameters) {
		try {
			TaEmotSvarResponseType response = new TaEmotSvarResponseType();

			// Transform payload to xml string
			String payload = JAXB_UTIL.marshal(response, "urn:riv:fk:vardgivare:sjukvard:TaEmotSvarResponder:1", "TaEmotSvarSvar");
			
			String vardenhetHsaId = null;
			boolean isFromFK = false;

			// Store answer in a map with an array with vårdenhet HSA-id as the key. Answers can come from both directions so add this behaviour
			if (parameters.getFKSKLTaEmotSvarAnrop().getAdressering().getMottagare().getOrganisation().getEnhet() != null) {
				// Answers from FK
				vardenhetHsaId = parameters.getFKSKLTaEmotSvarAnrop().getAdressering().getMottagare().getOrganisation().getEnhet().getId().getValue();
				isFromFK = true;
			} else {
				// Question from Varden
				vardenhetHsaId = parameters.getFKSKLTaEmotSvarAnrop().getAdressering().getAvsandare().getOrganisation().getEnhet().getId().getValue();
				isFromFK = false;
			}
			
			if (isFromFK) {
				// Create an entry for this hsaid
				if (!answerMapFK.containsKey(vardenhetHsaId)) {
					List<String> questions = new ArrayList<String>();
					answerMapFK.put(vardenhetHsaId, questions);
				}
					
				// Add question for this key
				List<String> questions = answerMapFK.get(vardenhetHsaId);
				questions.add(payload);
				
				// Print out all questions for this id from FK
				for(int i = 0; i < questions.size(); i++) {
					logger.debug("Answers from FK, index:" + i + ". Value: " + questions.get(i));
				}
				
			} else {
				// Create an entry for this hsaid
				if (!answerMapVard.containsKey(vardenhetHsaId)) {
					List<String> questions = new ArrayList<String>();
					answerMapVard.put(vardenhetHsaId, questions);
				}
					
				// Add question for this key
				List<String> questions = answerMapVard.get(vardenhetHsaId);
				questions.add(payload);
				
				// Print out all questions for this id
				for(int i = 0; i < questions.size(); i++) {
					logger.debug("Answers from Varden, index:" + i + ". Value: " + questions.get(i));
				}				
			}

			return response;
		} catch (Exception e) {
			System.out.println("Error occured: " + e);
			return null;
		}
	}
}
			

