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
package se.skl.skltpservices.adapter.fk.sendmedcertquestion;

import iso.v21090.dt.v1.II;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;

import org.mule.module.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.inera.ifv.insuranceprocess.healthreporting.qa.v1.VardAdresseringsType;
import se.inera.ifv.insuranceprocess.healthreporting.revokemedicalcertificateresponder.v1.RevokeMedicalCertificateRequestType;
import se.inera.ifv.insuranceprocess.healthreporting.revokemedicalcertificateresponder.v1.RevokeType;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.QuestionToFkType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.EnhetType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.PatientType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.VardgivareType;

public class RevokeCertificate extends Thread {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	QuestionToFkType question;
	String logicalAddress;

	MuleClient muleClient;
	
	public RevokeCertificate(QuestionToFkType question, String logicalAddress, MuleClient muleClient) {
		super();
		this.question = question;
		this.logicalAddress = logicalAddress;
		this.muleClient = muleClient;
	}

	@Override
	public void run() {				
		try {
			
			AttributedURIType logicalAddressHeader = new AttributedURIType();
			logicalAddressHeader.setValue(logicalAddress);	
			RevokeMedicalCertificateRequestType request = new RevokeMedicalCertificateRequestType();

			// Set revoke information 
			request.setRevoke(getRevokeData(question));
			
			// Create payload to webservice
			Object[] payloadOut = new Object[] {logicalAddressHeader, request};

			muleClient.send("revokeEndpoint", payloadOut, null);
			logger.info("Sent RevokeMedicalCertificate for certificate with id = " + question.getLakarutlatande().getLakarutlatandeId() + ".");
			
		} catch (Exception e) {
			logger.error("RevokeMedicalCertificate call exception: " + e.getMessage());
		}		
	}
	
	private static RevokeType getRevokeData(QuestionToFkType question) throws Exception {
		RevokeType meddelande = new RevokeType();

		// Avsandare
		VardAdresseringsType avsandare = new VardAdresseringsType();		
		HosPersonalType hosPersonal = new HosPersonalType();
		EnhetType enhet = new EnhetType();	
		II enhetsId = new II();
		enhetsId.setRoot("1.2.752.129.2.1.4.1");
		enhetsId.setExtension(question.getAdressVard().getHosPersonal().getEnhet().getEnhetsId().getExtension());
		enhet.setEnhetsId(enhetsId);
		enhet.setEnhetsnamn(question.getAdressVard().getHosPersonal().getEnhet().getEnhetsnamn());
		VardgivareType vardgivare = new VardgivareType();
		vardgivare.setVardgivarnamn(question.getAdressVard().getHosPersonal().getEnhet().getVardgivare().getVardgivarnamn());
		II vardgivareId = new II();
		vardgivareId.setRoot("1.2.752.129.2.1.4.1");
		vardgivareId.setExtension(question.getAdressVard().getHosPersonal().getEnhet().getVardgivare().getVardgivareId().getExtension());
		vardgivare.setVardgivareId(vardgivareId);
		enhet.setVardgivare(vardgivare);
		hosPersonal.setEnhet(enhet);
		hosPersonal.setFullstandigtNamn(question.getAdressVard().getHosPersonal().getFullstandigtNamn());
		II personalId = new II();
		personalId.setRoot("1.2.752.129.2.1.4.1");
		personalId.setExtension(question.getAdressVard().getHosPersonal().getPersonalId().getExtension());
		hosPersonal.setPersonalId(personalId);
		avsandare.setHosPersonal(hosPersonal);
		meddelande.setAdressVard(avsandare);
				
		// Avsant tidpunkt - nu
		meddelande.setAvsantTidpunkt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

		// Set lakarutlatande enkel fran varden
		meddelande.setVardReferensId(question.getVardReferensId());
		LakarutlatandeEnkelType lakarutlatandeEnkel = new LakarutlatandeEnkelType();
		PatientType patient = new PatientType();
		II personId = new II();
		personId.setRoot("1.2.752.129.2.1.3.1"); // OID för samordningsnummer är 1.2.752.129.2.1.3.3.
		personId.setExtension(question.getLakarutlatande().getPatient().getPersonId().getExtension());
		patient.setPersonId(personId);
		patient.setFullstandigtNamn(question.getLakarutlatande().getPatient().getFullstandigtNamn()); 
		lakarutlatandeEnkel.setPatient(patient);
		lakarutlatandeEnkel.setLakarutlatandeId(question.getLakarutlatande().getLakarutlatandeId());
		lakarutlatandeEnkel.setSigneringsTidpunkt(question.getLakarutlatande().getSigneringsTidpunkt());
		meddelande.setLakarutlatande(lakarutlatandeEnkel);

		return meddelande;
	}	
}




