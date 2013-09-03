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

import iso.v21090.dt.v1.II;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.InnehallType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.VardAdresseringsType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestion.v1.rivtabp20.SendMedicalCertificateQuestionResponderInterface;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestion.v1.rivtabp20.SendMedicalCertificateQuestionResponderService;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.QuestionToFkType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionResponseType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionType;
import se.skl.riv.insuranceprocess.healthreporting.v2.EnhetType;
import se.skl.riv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.skl.riv.insuranceprocess.healthreporting.v2.PatientType;
import se.skl.riv.insuranceprocess.healthreporting.v2.VardgivareType;

public class RevokeTransformTestConsumer {

	SendMedicalCertificateQuestionResponderInterface service;

	public RevokeTransformTestConsumer(String endpointAdress) {
		URL url = createEndpointUrlFromServiceAddress(endpointAdress);
		service = new SendMedicalCertificateQuestionResponderService(url).getSendMedicalCertificateQuestionResponderPort();
	}

	public SendMedicalCertificateQuestionResponseType sendMCQuestion(Amnetyp amne) throws DatatypeConfigurationException {

		try {
			SendMedicalCertificateQuestionType request = new SendMedicalCertificateQuestionType();
			request.setQuestion(getQuestion(amne));
	
			AttributedURIType adressing = new AttributedURIType();
			adressing.setValue("LOGICALADRESS");
	
			return service.sendMedicalCertificateQuestion(adressing, request);
		} catch (Exception ex) {
			System.out.println("Exception=" + ex.getMessage());
			return null;
		}
	}

	private static QuestionToFkType getQuestion(Amnetyp amne) throws Exception {
		QuestionToFkType meddelande = new QuestionToFkType();
		
		// Avsandare
		VardAdresseringsType avsandare = new VardAdresseringsType();		
		HosPersonalType hosPersonal = new HosPersonalType();
		EnhetType enhet = new EnhetType();	
		II enhetsId = new II();
		enhetsId.setRoot("1.2.752.129.2.1.4.1");
		enhetsId.setExtension("Enkopings lasaretts HSA-ID");
		enhet.setEnhetsId(enhetsId);
		enhet.setTelefonnummer("018-611 45 30");
		enhet.setPostadress("Akademiska sjukhuset");
		enhet.setPostnummer("751 85");
		enhet.setPostort("Uppsala");
		enhet.setEnhetsnamn("Kir mott UAS/KIR");
		VardgivareType vardgivare = new VardgivareType();
		vardgivare.setVardgivarnamn("Landstinget i Uppsala");
		II vardgivareId = new II();
		vardgivareId.setRoot("1.2.752.129.2.1.4.1");
		vardgivareId.setExtension("Uppsala landstings HSA-ID");
		vardgivare.setVardgivareId(vardgivareId);
		enhet.setVardgivare(vardgivare);
		hosPersonal.setEnhet(enhet);
		hosPersonal.setFullstandigtNamn("Erik Aselius");
		II personalId = new II();
		personalId.setRoot("1.2.752.129.2.1.4.1");
		personalId.setExtension("Personal HSA-ID");
		hosPersonal.setPersonalId(personalId);
		avsandare.setHosPersonal(hosPersonal);
		meddelande.setAdressVard(avsandare);
				
		// Avsant tidpunkt - nu
		meddelande.setAvsantTidpunkt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

		// Set lakarutlatande enkel fran varden
		meddelande.setVardReferensId("Referens till fraga fran varden");
		LakarutlatandeEnkelType lakarutlatandeEnkel = new LakarutlatandeEnkelType();
		PatientType patient = new PatientType();
		II personId = new II();
		personId.setRoot("1.2.752.129.2.1.3.1"); // OID for samordningsnummer ar 1.2.752.129.2.1.3.3.
		personId.setExtension("19430811-7094");
		patient.setPersonId(personId);
		patient.setFullstandigtNamn("Lab Testsson"); 
		lakarutlatandeEnkel.setPatient(patient);
		lakarutlatandeEnkel.setLakarutlatandeId("xxx");
		lakarutlatandeEnkel.setSigneringsTidpunkt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		meddelande.setLakarutlatande(lakarutlatandeEnkel);
	
		// Set amne
		meddelande.setAmne(amne);
		
		// Set meddelande - fraga
		InnehallType fraga = new InnehallType();
		fraga.setMeddelandeText("Meddelandetetext");
		fraga.setSigneringsTidpunkt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		meddelande.setFraga(fraga);
		
		return meddelande;
	}

	private static URL createEndpointUrlFromServiceAddress(String serviceAddress) {
		try {
			return new URL(serviceAddress + "?wsdl");
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}

}
