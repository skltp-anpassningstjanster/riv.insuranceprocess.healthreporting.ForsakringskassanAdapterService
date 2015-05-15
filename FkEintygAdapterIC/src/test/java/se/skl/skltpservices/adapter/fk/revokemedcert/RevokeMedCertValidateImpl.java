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
package se.skl.skltpservices.adapter.fk.revokemedcert;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jws.WebService;

import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.inera.ifv.insuranceprocess.healthreporting.revokemedicalcertificate.v1.rivtabp20.RevokeMedicalCertificateResponderInterface;
import se.inera.ifv.insuranceprocess.healthreporting.revokemedicalcertificateresponder.v1.RevokeMedicalCertificateRequestType;
import se.inera.ifv.insuranceprocess.healthreporting.revokemedicalcertificateresponder.v1.RevokeMedicalCertificateResponseType;
import se.inera.ifv.insuranceprocess.healthreporting.revokemedicalcertificateresponder.v1.RevokeType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.EnhetType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ErrorIdEnum;
import se.inera.ifv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.PatientType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.inera.ifv.insuranceprocess.healthreporting.v2.ResultOfCall;
import se.inera.ifv.insuranceprocess.healthreporting.v2.VardgivareType;

/**
 * Validation class that will certify a webservice call made for an answer regarding a medical certificate.. We will check mandatory/optional fields and all other declared rules.
 * @author matsek
 *
 */

@WebService(
		serviceName = "RevokeMedicalCertificateResponderService", 
		endpointInterface="se.inera.ifv.insuranceprocess.healthreporting.revokemedicalcertificate.v1.rivtabp20.RevokeMedicalCertificateResponderInterface", 
		portName = "RevokeMedicalCertificateResponderPort", 
		targetNamespace = "urn:riv:insuranceprocess:healthreporting:RevokeMedicalCertificate:1:rivtabp20",
		wsdlLocation = "schemas/vard/interactions/RevokeMedicalCertificateInteraction/RevokeMedicalCertificateInteraction_1.0_rivtabp20.wsdl")
public class RevokeMedCertValidateImpl implements RevokeMedicalCertificateResponderInterface {

	public RevokeMedicalCertificateResponseType revokeMedicalCertificate(
			AttributedURIType logicalAddress,
			RevokeMedicalCertificateRequestType parameters) {

		// List of validation errors
		ArrayList<String> validationErrors = new ArrayList<String>();

		// Create a response and set result of validation            
		RevokeMedicalCertificateResponseType outResponse = new RevokeMedicalCertificateResponseType();
		ResultOfCall outResCall = new ResultOfCall();
		outResponse.setResult(outResCall);

		// Validate incoming request
		try {
			// Check that we got any data at all
			if (parameters == null) {
				validationErrors.add("No RevokeMedicalCertificate found in incoming data!");
				throw new Exception();
			}
			
			// Check that we got an answer element
			if (parameters.getRevoke() == null) {
				validationErrors.add("No Revoke element found in incoming request data!");
				throw new Exception();
			}
			
			RevokeType inRevoke = parameters.getRevoke();
			
			/**
			 *  Check meddelande data + lakarutlatande reference
			 */
			
			// Meddelande id vården - mandatory
			if ( inRevoke.getVardReferensId() == null ||
					inRevoke.getVardReferensId().length() < 1 ) {
				 validationErrors.add("No vardReferens-id found!");				
			}
			
			// Avsänt tidpunkt - mandatory
            if (inRevoke.getAvsantTidpunkt() == null || !inRevoke.getAvsantTidpunkt().isValid()) {
				validationErrors.add("No or wrong avsantTidpunkt found!");				
            }
						
			// Läkarutlåtande referens - mandatory
            if (inRevoke.getLakarutlatande() == null ) {
				validationErrors.add("No lakarutlatande element found!");	
				throw new Exception();
            }
            LakarutlatandeEnkelType inLakarUtlatande = inRevoke.getLakarutlatande();
            
			// Läkarutlåtande referens - id - mandatory
			if ( inLakarUtlatande.getLakarutlatandeId() == null ||
				inLakarUtlatande.getLakarutlatandeId().length() < 1 ) {
				validationErrors.add("No lakarutlatande-id found!");				
			}

			// Läkarutlåtande referens - signeringsTidpunkt - mandatory
            if (inLakarUtlatande.getSigneringsTidpunkt() == null || !inLakarUtlatande.getSigneringsTidpunkt().isValid()) {
				validationErrors.add("No or wrong lakarutlatande-signeringsTidpunkt found!");				
            }

			// Läkarutlåtande referens - patient - mandatory
            if (inLakarUtlatande.getPatient() == null ) {
				validationErrors.add("No lakarutlatande patient element found!");	
				throw new Exception();
            }
            PatientType inPatient = inLakarUtlatande.getPatient();
            
			// Läkarutlåtande referens - patient - personid mandatory
            // Check patient id - mandatory
			if (inPatient.getPersonId() == null ||	
				inPatient.getPersonId().getExtension() == null ||	
				inPatient.getPersonId().getExtension().length() < 1) {
				validationErrors.add("No lakarutlatande-Patient Id found!");								
			}
			
			// Check patient o.i.d.
			if (inPatient.getPersonId() == null ||	
				inPatient.getPersonId().getRoot() == null ||	
				(!inPatient.getPersonId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.3.1") && !inPatient.getPersonId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.3.3"))) {
					validationErrors.add("Wrong o.i.d. for Patient Id! Should be 1.2.752.129.2.1.3.1 or 1.2.752.129.2.1.3.3");								
				}
			String inPersonnummer = inPatient.getPersonId().getExtension();

	        // Check format of patient id - personnummer valid format is 19121212-1212 or 19121212+1212
//			if (!Pattern.matches("[0-9]{8}[-+][0-9]{4}", inPersonnummer) ) {
//				validationErrors.add("Wrong format for person-id! Valid format is YYYYMMDD-XXXX or YYYYMMDD+XXXX.");												
//			}
            
			// Läkarutlåtande referens - patient - namn - mandatory
			if (inPatient.getFullstandigtNamn() == null || inPatient.getFullstandigtNamn().length() < 1 ) {
				validationErrors.add("No lakarutlatande Patient fullstandigtNamn elements found or set!");								
			}
								
			/**
			 *  Check avsändar data.
			 */
			if (inRevoke.getAdressVard() == null) {
				validationErrors.add("No adressVard element found!");				
				throw new Exception();
			}
			if ( inRevoke.getAdressVard().getHosPersonal() == null) {
				validationErrors.add("No adressVard - hosPersonal element found!");				
				throw new Exception();
			}	
			HosPersonalType inHoSP = inRevoke.getAdressVard().getHosPersonal();
			
		    // Check lakar id - mandatory
	        if (inHoSP.getPersonalId() == null || 
	        	inHoSP.getPersonalId().getExtension() == null ||
	        	inHoSP.getPersonalId().getExtension().length() < 1) {
				validationErrors.add("No personal-id found!");	            	
	        }
	        // Check lakar id o.i.d.
	        if (inHoSP.getPersonalId() == null || 
	        	inHoSP.getPersonalId().getRoot() == null ||
	            !inHoSP.getPersonalId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
				validationErrors.add("Wrong o.i.d. for personalId! Should be 1.2.752.129.2.1.4.1");								
	        }
	        
	        // Check lakarnamn - mandatory
			if (inHoSP.getFullstandigtNamn() == null || inHoSP.getFullstandigtNamn().length() < 1 ) {
				validationErrors.add("No skapadAvHosPersonal fullstandigtNamn elements found or set!");								
			}

	        // Check that we got a enhet element
	        if (inHoSP.getEnhet() == null) {
				validationErrors.add("No enhet element found!");	  
				throw new Exception();
	        }
	        EnhetType inEnhet = inHoSP.getEnhet() ;
	       
	        // Check enhets id - mandatory
	        if (inEnhet.getEnhetsId() == null ||
	        	inEnhet.getEnhetsId().getExtension() == null ||
	        	inEnhet.getEnhetsId().getExtension().length() < 1) {
				validationErrors.add("No enhets-id found!");	            	
	        }
	        // Check enhets o.i.d
	        if (inEnhet.getEnhetsId() == null || 
	        	inEnhet.getEnhetsId().getRoot() == null ||
	            !inEnhet.getEnhetsId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
				validationErrors.add("Wrong o.i.d. for enhetsId! Should be 1.2.752.129.2.1.4.1");								
	        }
	        
	        // Check enhetsnamn - mandatory
	        if (inEnhet.getEnhetsnamn() == null || 
	        	inEnhet.getEnhetsnamn().length() < 1) {
	        	validationErrors.add("No enhetsnamn found!");	            	
	        }

	        // Check that we got a vardgivare element
	        if (inEnhet.getVardgivare() == null) {
				validationErrors.add("No vardgivare element found!");	  
				throw new Exception();
	        }
	        VardgivareType inVardgivare = inEnhet.getVardgivare();
	       
	        // Check vardgivare id - mandatory
	        if (inVardgivare.getVardgivareId() == null ||
	        	inVardgivare.getVardgivareId().getExtension() == null ||
	        	inVardgivare.getVardgivareId().getExtension().length() < 1) {
				validationErrors.add("No vardgivare-id found!");	            	
	        }
	        // Check vardgivare o.i.d.
	        if (inVardgivare.getVardgivareId() == null || 
	        	inVardgivare.getVardgivareId().getRoot() == null ||
	            !inVardgivare.getVardgivareId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
	        	validationErrors.add("Wrong o.i.d. for vardgivareId! Should be 1.2.752.129.2.1.4.1");								
	        }

	        // Check vardgivarename - mandatory
	        if (inVardgivare.getVardgivarnamn() == null || 
	        	inVardgivare.getVardgivarnamn().length() < 1) {
				validationErrors.add("No vardgivarenamn found!");	            	
	        }
			
			// Check if we got any validation errors that not caused an Exception
			if (validationErrors.size() > 0) {
				throw new Exception();
			} 
			
			// No validation errors! Return OK!            
			outResCall.setResultCode(ResultCodeEnum.OK);
			outResponse.setResult(outResCall);

			return outResponse;
		} catch (Exception e) {
			outResCall.setErrorText(getValidationErrors(validationErrors));
			outResCall.setErrorId(ErrorIdEnum.VALIDATION_ERROR);
			outResCall.setResultCode(ResultCodeEnum.ERROR);
			return outResponse;
		}
	}
	
	private String getValidationErrors(ArrayList<String> validationErrors) {
		int i = 1;
		StringBuffer validationString = new StringBuffer();
		Iterator<String> iterValidationErrors = validationErrors.iterator();
		validationString.append("Validation error " + i++ + ":");
		validationString.append((String)iterValidationErrors.next());
		while (iterValidationErrors.hasNext()) {
			validationString.append("\n\rValidation error " + i++ + ":");
			validationString.append((String)iterValidationErrors.next());
		}
		return validationString.toString();
	}
	
}