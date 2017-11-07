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
package se.skl.skltpservices.adapter.fk.sendmedcertanswer;

import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.w3.wsaddressing10.AttributedURIType;

import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarType;
import se.fk.vardgivare.sjukvard.v1.Adress;
import se.fk.vardgivare.sjukvard.v1.Adressering;
import se.fk.vardgivare.sjukvard.v1.Adressering.Avsandare;
import se.fk.vardgivare.sjukvard.v1.Adressering.Mottagare;
import se.fk.vardgivare.sjukvard.v1.Amne;
import se.fk.vardgivare.sjukvard.v1.Enhet;
import se.fk.vardgivare.sjukvard.v1.Epostadress;
import se.fk.vardgivare.sjukvard.v1.InternIdentitetsbeteckning;
import se.fk.vardgivare.sjukvard.v1.Kontaktuppgifter;
import se.fk.vardgivare.sjukvard.v1.Lakarintygsreferens;
import se.fk.vardgivare.sjukvard.v1.Land;
import se.fk.vardgivare.sjukvard.v1.Meddelande;
import se.fk.vardgivare.sjukvard.v1.Namn;
import se.fk.vardgivare.sjukvard.v1.Organisation;
import se.fk.vardgivare.sjukvard.v1.Patient;
import se.fk.vardgivare.sjukvard.v1.Person;
import se.fk.vardgivare.sjukvard.v1.Postadress;
import se.fk.vardgivare.sjukvard.v1.Postnummer;
import se.fk.vardgivare.sjukvard.v1.Postort;
import se.fk.vardgivare.sjukvard.v1.ReferensAdressering;
import se.fk.vardgivare.sjukvard.v1.TaEmotSvar;
import se.fk.vardgivare.sjukvard.v1.Telefon;
import se.inera.ifv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.inera.ifv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.inera.ifv.insuranceprocess.healthreporting.qa.v1.VardAdresseringsType;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.SendMedicalCertificateAnswerType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.EnhetType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.PatientType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.VardgivareType;

public class VardRequest2FkTransformer extends AbstractMessageTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private VardRequest2FkValidator validator = new VardRequest2FkValidator();
	private static final JaxbUtil jaxbUtil = new JaxbUtil(SendMedicalCertificateAnswerType.class);

	public VardRequest2FkTransformer() {
		super();
		registerSourceType(DataTypeFactory.create(Object.class));
	}

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		logger.info("Entering vard2fk send medical certificate answer transform");

		ResourceBundle rb = ResourceBundle.getBundle("fkdataSendMCAnswer");
		final String FK_ID = "2021005521";
		XMLStreamReader streamPayload = null;

		try {
			// Transform the XML payload into a JAXB object
			streamPayload = (XMLStreamReader) ((Object[]) message.getPayload())[1];
			SendMedicalCertificateAnswerType inRequest = (SendMedicalCertificateAnswerType) jaxbUtil
					.unmarshal(streamPayload);

			validator.validateRequest(inRequest);

			// Get receiver to adress from Mule property
			// String receiverId = (String)message.getProperty("receiverid");

			// Create new JAXB object for the outgoing data
			TaEmotSvarType outRequest = new TaEmotSvarType();
			TaEmotSvar outTaEmotSvar = new TaEmotSvar();
			outRequest.setFKSKLTaEmotSvarAnrop(outTaEmotSvar);

			// Transform between incoming and outgoing objects
			// Avsändare - Vården
			VardAdresseringsType inAvsandare = inRequest.getAnswer().getAdressVard();
			HosPersonalType inHoSPersonalAvsandare = inAvsandare.getHosPersonal();
			EnhetType inEnhetAvsandare = inHoSPersonalAvsandare.getEnhet();
			VardgivareType inVardgivareAvsandare = inEnhetAvsandare.getVardgivare();

			Avsandare outAvsandare = new Avsandare();
			Adressering outAdressering = new Adressering();
			outAdressering.setAvsandare(outAvsandare);
			outTaEmotSvar.setAdressering(outAdressering);

			ReferensAdressering referensId = new ReferensAdressering();
			referensId.setValue(inRequest.getAnswer().getVardReferensId());
			outAvsandare.setReferens(referensId);

			Organisation outOrganisationAvsandare = new Organisation();
			InternIdentitetsbeteckning outOrganisationIdAvsandare = new InternIdentitetsbeteckning();
			outOrganisationIdAvsandare.setValue(inVardgivareAvsandare.getVardgivareId().getExtension());
			outOrganisationAvsandare.setId(outOrganisationIdAvsandare);
			Namn outOrganisationNamnAvsandare = new Namn();
			outOrganisationNamnAvsandare.setValue(inVardgivareAvsandare.getVardgivarnamn());
			outOrganisationAvsandare.setNamn(outOrganisationNamnAvsandare);
			outAvsandare.setOrganisation(outOrganisationAvsandare);

			Enhet outEnhetAvsandare = new Enhet();
			InternIdentitetsbeteckning outEnhetIdAvsandare = new InternIdentitetsbeteckning();
			outEnhetIdAvsandare.setValue(inEnhetAvsandare.getEnhetsId().getExtension());
			outEnhetAvsandare.setId(outEnhetIdAvsandare);
			Namn outEnhetNamnAvsandare = new Namn();
			outEnhetNamnAvsandare.setValue(inEnhetAvsandare.getEnhetsnamn());
			outEnhetAvsandare.setNamn(outEnhetNamnAvsandare);

			// Check which optional fields that we got
			String postnummer = inEnhetAvsandare.getPostnummer();
			String postort = inEnhetAvsandare.getPostort();
			String postadress = inEnhetAvsandare.getPostadress();
			String ePost = inEnhetAvsandare.getEpost();
			String telefonnummer = inEnhetAvsandare.getTelefonnummer();

			// Check which adress information to set...
			Kontaktuppgifter outEnhetKontaktuppgifterAvsandare = new Kontaktuppgifter();

			// Start with adress, all fileds must be present!
			if ((postnummer != null && postnummer.length() > 0) && (postort != null && postort.length() > 0)
					&& (postadress != null && postadress.length() > 0)) {
				Adress outEnhetAdressAvsandare = new Adress();

				Postadress outEnhetPostadressAvsandare = new Postadress();
				outEnhetPostadressAvsandare.setValue(postadress);
				outEnhetAdressAvsandare.setPostadress(outEnhetPostadressAvsandare);

				Postnummer outEnhetPostnummerAvsandare = new Postnummer();
				outEnhetPostnummerAvsandare.setValue(postnummer);
				outEnhetAdressAvsandare.setPostnummer(outEnhetPostnummerAvsandare);

				Postort outEnhetPostortAvsandare = new Postort();
				outEnhetPostortAvsandare.setValue(postort);
				outEnhetAdressAvsandare.setPostort(outEnhetPostortAvsandare);

				Land land = new Land();
				land.setValue("Sverige");
				outEnhetAdressAvsandare.setLand(land);

				outEnhetKontaktuppgifterAvsandare.setAdress(outEnhetAdressAvsandare);
			}

			// Next telefonnummer
			if (telefonnummer != null && telefonnummer.length() > 0) {
				Telefon outEnhetTelefonAvsandare = new Telefon();
				outEnhetTelefonAvsandare.setValue(telefonnummer);
				outEnhetKontaktuppgifterAvsandare.setTelefon(outEnhetTelefonAvsandare);
			}

			// Last ePost
			if (ePost != null && ePost.length() > 0) {
				Epostadress epost = new Epostadress();
				epost.setValue(ePost);
				outEnhetKontaktuppgifterAvsandare.setEpost(epost);
			}

			outEnhetAvsandare.setKontaktuppgifter(outEnhetKontaktuppgifterAvsandare);
			outOrganisationAvsandare.setEnhet(outEnhetAvsandare);

			Person outPersonAvsandare = new Person();
			InternIdentitetsbeteckning outPersonIdAvsandare = new InternIdentitetsbeteckning();
			outPersonIdAvsandare.setValue(inHoSPersonalAvsandare.getPersonalId().getExtension());
			outPersonAvsandare.setId(outPersonIdAvsandare);
			outPersonAvsandare.setNamn(inHoSPersonalAvsandare.getFullstandigtNamn());
			outEnhetAvsandare.setPerson(outPersonAvsandare);

			// Mottagare - FK
			Mottagare outMottagare = new Mottagare();
			outAdressering.setMottagare(outMottagare);
			Organisation outOrganisationMottagare = new Organisation();
			outMottagare.setOrganisation(outOrganisationMottagare);

			ReferensAdressering referensIdFk = new ReferensAdressering();
			referensIdFk.setValue(inRequest.getAnswer().getFkReferensId());
			outAvsandare.setReferens(referensIdFk);

			Namn outOrganisationNamnMottagare = new Namn();
			outOrganisationNamnMottagare.setValue(rb.getString("FK"));
			outOrganisationMottagare.setNamn(outOrganisationNamnMottagare);
			InternIdentitetsbeteckning outOrganisationIdMottagare = new InternIdentitetsbeteckning();
			outOrganisationIdMottagare.setValue("202100-5521");
			outOrganisationMottagare.setId(outOrganisationIdMottagare);

			// Skickades
			outAdressering.setSkickades(inRequest.getAnswer().getAvsantTidpunkt());

			// Patient
			LakarutlatandeEnkelType inLakarutlatande = inRequest.getAnswer().getLakarutlatande();
			PatientType inPatient = inLakarutlatande.getPatient();
			Patient outPatient = new Patient();
			outPatient.setIdentifierare(inPatient.getPersonId().getExtension());
			outPatient.setNamn(inPatient.getFullstandigtNamn());
			outTaEmotSvar.setPatient(outPatient);

			// Ämne
			Amnetyp inAmne = inRequest.getAnswer().getAmne();
			Amne outAmne = new Amne();
			outAmne.setBeskrivning(transformAmneFromVarden(inAmne, rb));
			outAmne.setFritext(transformAmneFromVarden(inAmne, rb));
			outTaEmotSvar.setAmne(outAmne);

			// Läkarintyg referens
			Lakarintygsreferens outLakarintyg = new Lakarintygsreferens();
			outLakarintyg.setReferens(inLakarutlatande.getLakarutlatandeId());
			outLakarintyg.setSignerades(inLakarutlatande.getSigneringsTidpunkt());
			outTaEmotSvar.setLakarintyg(outLakarintyg);

			// Fraga
			Meddelande fraga = new Meddelande();
			fraga.setText(inRequest.getAnswer().getFraga().getMeddelandeText());
			fraga.setSignerades(inRequest.getAnswer().getFraga().getSigneringsTidpunkt());
			outTaEmotSvar.setFraga(fraga);

			// Svar
			Meddelande svar = new Meddelande();
			svar.setText(inRequest.getAnswer().getSvar().getMeddelandeText());
			svar.setSignerades(inRequest.getAnswer().getSvar().getSigneringsTidpunkt());
			outTaEmotSvar.setSvar(svar);

			AttributedURIType logicalAddressHeader = new AttributedURIType();
			logicalAddressHeader.setValue(FK_ID);

			Object[] payloadOut = new Object[] { logicalAddressHeader, outRequest };

			if (logger.isDebugEnabled()) {
				logger.debug("transformed payload to: " + payloadOut);
			}

			logger.info("Exiting vard2fk send medical certificate answer transform");
			return payloadOut;
		} catch (Exception e) {
			logger.error("Transform exception:" + e.getMessage());
			throw new TransformerException(MessageFactory.createStaticMessage(e.getMessage()));
		} finally {
			if (streamPayload != null) {
				try {
					streamPayload.close();
				} catch (XMLStreamException e) {
				}
			}
		}
	}

	private String transformAmneFromVarden(Amnetyp inAmne, ResourceBundle rb) {
		if (inAmne.compareTo(Amnetyp.ARBETSTIDSFORLAGGNING) == 0) {
			return rb.getString("ARBTIDFORL");
		} else if (inAmne.compareTo(Amnetyp.AVSTAMNINGSMOTE) == 0) {
			return rb.getString("AVSTAMMOTE");
		} else if (inAmne.compareTo(Amnetyp.KOMPLETTERING_AV_LAKARINTYG) == 0) {
			return "Komplettering";
		} else if (inAmne.compareTo(Amnetyp.KONTAKT) == 0) {
			return "Kontakt";
		} else if (inAmne.compareTo(Amnetyp.OVRIGT) == 0) {
			return rb.getString("OVRIGT");
		} else if (inAmne.compareTo(Amnetyp.MAKULERING_AV_LAKARINTYG) == 0) {
			return "Makulering";
		} else {
			return rb.getString("OVRIGT");
		}
	}

}