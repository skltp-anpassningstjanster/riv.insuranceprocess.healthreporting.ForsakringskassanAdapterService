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
package se.skl.skltpservices.adapter.fk.vardgivare.sjukvard.taemotfraga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.w3.wsaddressing10.AttributedURIType;

import se.fk.vardgivare.sjukvard.taemotfraga.v1.rivtabp20.TaEmotFragaResponderInterface;
import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaResponseType;
import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaType;

@WebService(
		serviceName = "TaEmotFragaResponderService", 
		endpointInterface="se.fk.vardgivare.sjukvard.taemotfraga.v1.rivtabp20.TaEmotFragaResponderInterface", 
		portName = "TaEmotFragaResponderPort", 
		targetNamespace = "urn:riv:fk:vardgivare:sjukvard:TaEmotFraga:1:rivtabp20",
		wsdlLocation = "schemas/fk/TaEmotFragaInteraction_1.0_rivtabp20.wsdl")
public class TaEmotFragaImpl implements TaEmotFragaResponderInterface {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final JaxbUtil JAXB_UTIL = new JaxbUtil(TaEmotFragaResponseType.class);

	
	static Map<String, List<String>> questionMapFK = new HashMap<String, List<String>>(); 
	static Map<String, List<String>> questionMapVard = new HashMap<String, List<String>>(); 
	
	public TaEmotFragaResponseType taEmotFraga(
			AttributedURIType logicalAddress, TaEmotFragaType parameters) {
		try {
			TaEmotFragaResponseType response = new TaEmotFragaResponseType();
			
			// Transform payload to xml string
			String payload = JAXB_UTIL.marshal(response, "urn:riv:fk:vardgivare:sjukvard:TaEmotFragaResponder:1", "TaEmotFraga");
			
			String vardenhetHsaId = null;
			boolean isFromFK = false;

			// Store question in an map with an array with vårdenhet HSA-id as the key. Questions can come from both directions so add this behaviour
			if (parameters.getFKSKLTaEmotFragaAnrop().getAdressering().getMottagare().getOrganisation().getEnhet() != null) {
				// Question from FK
				vardenhetHsaId = parameters.getFKSKLTaEmotFragaAnrop().getAdressering().getMottagare().getOrganisation().getEnhet().getId().getValue();
				isFromFK = true;
			} else {
				// Question from Varden
				vardenhetHsaId = parameters.getFKSKLTaEmotFragaAnrop().getAdressering().getAvsandare().getOrganisation().getEnhet().getId().getValue();
				isFromFK = false;
			}
			
			if (isFromFK) {
				// Create an entry for this hsaid
				if (!questionMapFK.containsKey(vardenhetHsaId)) {
					List<String> questions = new ArrayList<String>();
					questionMapFK.put(vardenhetHsaId, questions);
				}
					
				// Add question for this key
				List<String> questions = questionMapFK.get(vardenhetHsaId);
				questions.add(payload);
				
				// Print out all questions for this id from FK
				for(int i = 0; i < questions.size(); i++) {
					logger.debug("Questions from FK, index:" + i + ". Value: " + questions.get(i));
				}
				
				// TEST!!!
				
			} else {
				// Create an entry for this hsaid
				if (!questionMapVard.containsKey(vardenhetHsaId)) {
					List<String> questions = new ArrayList<String>();
					questionMapVard.put(vardenhetHsaId, questions);
				}
					
				// Add question for this key
				List<String> questions = questionMapVard.get(vardenhetHsaId);
				questions.add(payload);
				
				// Print out all questions for this id
				for(int i = 0; i < questions.size(); i++) {
					logger.debug("Questions from Varden, index:" + i + ". Value: " + questions.get(i));
				}				
			}			
			
			return response;
		} catch (Exception e) {
			logger.error("Error occured: " + e);
			return null;
		}
	}
}