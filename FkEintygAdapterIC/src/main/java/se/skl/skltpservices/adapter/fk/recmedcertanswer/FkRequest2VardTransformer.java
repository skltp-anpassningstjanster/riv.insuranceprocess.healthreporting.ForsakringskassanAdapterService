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
package se.skl.skltpservices.adapter.fk.recmedcertanswer;

import iso.v21090.dt.v1.II;

import javax.xml.datatype.XMLGregorianCalendar;
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
import se.fk.vardgivare.sjukvard.v1.Adressering.Avsandare;
import se.fk.vardgivare.sjukvard.v1.Adressering.Mottagare;
import se.fk.vardgivare.sjukvard.v1.Amne;
import se.fk.vardgivare.sjukvard.v1.Enhet;
import se.fk.vardgivare.sjukvard.v1.Lakarintygsreferens;
import se.fk.vardgivare.sjukvard.v1.Organisation;
import se.fk.vardgivare.sjukvard.v1.Patient;
import se.fk.vardgivare.sjukvard.v1.Person;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.FkKontaktType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.InnehallType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.VardAdresseringsType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificateanswerresponder.v1.AnswerFromFkType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificateanswerresponder.v1.ReceiveMedicalCertificateAnswerType;
import se.skl.riv.insuranceprocess.healthreporting.v2.EnhetType;
import se.skl.riv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.skl.riv.insuranceprocess.healthreporting.v2.PatientType;
import se.skl.riv.insuranceprocess.healthreporting.v2.VardgivareType;

public class FkRequest2VardTransformer extends AbstractMessageTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final JaxbUtil jaxbUtil = new JaxbUtil(TaEmotSvarType.class);

	public FkRequest2VardTransformer() {
		super();
		registerSourceType(DataTypeFactory.create(Object.class));
	}

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		XMLStreamReader streamPayload = null;

		logger.info("Entering fk2vard receive medical certificate answer transform");

		try {
			// Transform the XML payload into a JAXB object
			streamPayload = (XMLStreamReader) ((Object[]) message.getPayload())[1];
			TaEmotSvarType inRequest = (TaEmotSvarType) jaxbUtil.unmarshal(streamPayload);

			// Create new JAXB object for the outgoing data
			ReceiveMedicalCertificateAnswerType outRequest = new ReceiveMedicalCertificateAnswerType();
			AnswerFromFkType outMeddelande = new AnswerFromFkType();
			outRequest.setAnswer(outMeddelande);

			// Transform between incoming and outgoing objects
			// Avsändare - FK, Create contact info from FK as separate strings
			Avsandare inAvsandare = inRequest.getFKSKLTaEmotSvarAnrop().getAdressering().getAvsandare();
			Organisation inOrganisationAvsandare = inAvsandare.getOrganisation();

			// The only contact data from FK is the name of the person handling
			// this question
			if (inAvsandare.getOrganisation() != null && inAvsandare.getOrganisation().getEnhet() != null
					&& inAvsandare.getOrganisation().getEnhet().getPerson() != null) {
				// Check if we got any name data
				if (inAvsandare.getOrganisation().getEnhet().getPerson().getNamn() != null
						&& inAvsandare.getOrganisation().getEnhet().getPerson().getNamn().length() > 0) {
					FkKontaktType kontaktInfo = new FkKontaktType();
					kontaktInfo
							.setKontakt("Kontakt: " + inAvsandare.getOrganisation().getEnhet().getPerson().getNamn());
					outMeddelande.getFkKontaktInfo().add(kontaktInfo);
				} else {
					String forNamn = "";
					String efterNamn = "";
					if (inAvsandare.getOrganisation().getEnhet().getPerson().getFornamn() != null
							&& inAvsandare.getOrganisation().getEnhet().getPerson().getFornamn().length() > 0) {
						forNamn = inAvsandare.getOrganisation().getEnhet().getPerson().getFornamn();
					}
					if (inAvsandare.getOrganisation().getEnhet().getPerson().getEfternamn() != null
							&& inAvsandare.getOrganisation().getEnhet().getPerson().getEfternamn().length() > 0) {
						efterNamn = inAvsandare.getOrganisation().getEnhet().getPerson().getEfternamn();
					}
					FkKontaktType kontaktInfo = new FkKontaktType();
					kontaktInfo.setKontakt("Kontakt: " + forNamn + " " + efterNamn);
					outMeddelande.getFkKontaktInfo().add(kontaktInfo);
				}
			}

			// Mottagare - Vården
			Mottagare inMottagare = inRequest.getFKSKLTaEmotSvarAnrop().getAdressering().getMottagare();
			Organisation inOrganisationMottagare = inMottagare.getOrganisation();
			Enhet inEnhetMottagare = inOrganisationMottagare.getEnhet();
			Person inPersonMottagare = inEnhetMottagare.getPerson();

			VardAdresseringsType outMottagare = new VardAdresseringsType();
			outMeddelande.setAdressVard(outMottagare);
			HosPersonalType outHosPersonalMottagare = new HosPersonalType();
			outMottagare.setHosPersonal(outHosPersonalMottagare);

			if (inPersonMottagare.getNamn() != null && inPersonMottagare.getNamn().length() > 0) {
				outHosPersonalMottagare.setFullstandigtNamn(inPersonMottagare.getNamn());
			} else {
				outHosPersonalMottagare.setFullstandigtNamn(inPersonMottagare.getFornamn() + " "
						+ inPersonMottagare.getEfternamn());
			}
			II outPersonalIdMottagare = new II();
			outPersonalIdMottagare.setRoot("1.2.752.129.2.1.4.1");
			outPersonalIdMottagare.setExtension(inPersonMottagare.getId().getValue());
			outHosPersonalMottagare.setPersonalId(outPersonalIdMottagare);

			EnhetType outEnhetMottagare = new EnhetType();
			II outEnhetsIdMottagare = new II();
			outEnhetsIdMottagare.setRoot("1.2.752.129.2.1.4.1");
			// TODO Skydda mot null values!!!
			outEnhetsIdMottagare.setExtension(inEnhetMottagare.getId().getValue());
			outEnhetMottagare.setEnhetsId(outEnhetsIdMottagare);

			if (inEnhetMottagare.getKontaktuppgifter() != null) {

				// Address
				if (inEnhetMottagare.getKontaktuppgifter().getAdress() != null) {
					if (inEnhetMottagare.getKontaktuppgifter().getAdress().getPostadress() != null
							&& inEnhetMottagare.getKontaktuppgifter().getAdress().getPostadress().getValue() != null
							&& inEnhetMottagare.getKontaktuppgifter().getAdress().getPostadress().getValue().length() > 0) {
						outEnhetMottagare.setPostadress(inEnhetMottagare.getKontaktuppgifter().getAdress()
								.getPostadress().getValue());
					}
					if (inEnhetMottagare.getKontaktuppgifter().getAdress().getPostnummer() != null
							&& inEnhetMottagare.getKontaktuppgifter().getAdress().getPostnummer().getValue() != null
							&& inEnhetMottagare.getKontaktuppgifter().getAdress().getPostnummer().getValue().length() > 0) {
						outEnhetMottagare.setPostnummer(inEnhetMottagare.getKontaktuppgifter().getAdress()
								.getPostnummer().getValue());
					}
					if (inEnhetMottagare.getKontaktuppgifter().getAdress().getPostort() != null
							&& inEnhetMottagare.getKontaktuppgifter().getAdress().getPostort().getValue() != null
							&& inEnhetMottagare.getKontaktuppgifter().getAdress().getPostort().getValue().length() > 0) {
						outEnhetMottagare.setPostort(inEnhetMottagare.getKontaktuppgifter().getAdress().getPostort()
								.getValue());
					}
				}

				// Telefon
				if (inEnhetMottagare.getKontaktuppgifter().getTelefon() != null
						&& inEnhetMottagare.getKontaktuppgifter().getTelefon().getValue() != null
						&& inEnhetMottagare.getKontaktuppgifter().getTelefon().getValue().length() > 0) {
					outEnhetMottagare.setTelefonnummer(inEnhetMottagare.getKontaktuppgifter().getTelefon().getValue());
				}

				// eMail
				if (inEnhetMottagare.getKontaktuppgifter().getEpost() != null
						&& inEnhetMottagare.getKontaktuppgifter().getEpost().getValue() != null
						&& inEnhetMottagare.getKontaktuppgifter().getEpost().getValue().length() > 0) {
					outEnhetMottagare.setEpost(inEnhetMottagare.getKontaktuppgifter().getEpost().getValue());
				}
			}

			outEnhetMottagare.setEnhetsnamn(inEnhetMottagare.getNamn().getValue());
			outHosPersonalMottagare.setEnhet(outEnhetMottagare);

			VardgivareType outVardgivareMottagare = new VardgivareType();
			outVardgivareMottagare.setVardgivarnamn(inOrganisationMottagare.getNamn().getValue());
			II outVardgivareIdMottagare = new II();
			outVardgivareIdMottagare.setRoot("1.2.752.129.2.1.4.1");
			outVardgivareIdMottagare.setExtension(inOrganisationMottagare.getId().getValue());
			outVardgivareMottagare.setVardgivareId(outVardgivareIdMottagare);
			outEnhetMottagare.setVardgivare(outVardgivareMottagare);

			// Avsänt tidpunkt
			XMLGregorianCalendar inSkickades = inRequest.getFKSKLTaEmotSvarAnrop().getAdressering().getSkickades();
			outMeddelande.setAvsantTidpunkt(inSkickades);

			// Set läkarutlåtande enkel från vården
			Lakarintygsreferens inLakarutlatande = inRequest.getFKSKLTaEmotSvarAnrop().getLakarintyg();
			Patient inPatient = inRequest.getFKSKLTaEmotSvarAnrop().getPatient();

			LakarutlatandeEnkelType outLakarutlatandeEnkel = new LakarutlatandeEnkelType();
			PatientType outPatient = new PatientType();
			II outPersonId = new II();
			// TODO Calculate if samordingsnummer!
			outPersonId.setRoot("1.2.752.129.2.1.3.1"); // OID för
														// samordningsnummer är
														// 1.2.752.129.2.1.3.3.
			outPersonId.setExtension(inPatient.getIdentifierare());
			outPatient.setPersonId(outPersonId);
			// Check if name is in separate fields or not!
			if (inPatient.getNamn() != null && inPatient.getNamn().length() > 0) {
				outPatient.setFullstandigtNamn(inPatient.getNamn());
			} else {
				outPatient.setFullstandigtNamn(inPatient.getFornamn() + " " + inPatient.getEfternamn());
			}
			outLakarutlatandeEnkel.setPatient(outPatient);
			outLakarutlatandeEnkel.setLakarutlatandeId(inLakarutlatande.getReferens());
			outLakarutlatandeEnkel.setSigneringsTidpunkt(inLakarutlatande.getSignerades());
			outMeddelande.setLakarutlatande(outLakarutlatandeEnkel);

			// Set Försäkringskassans id
			outMeddelande.setFkReferensId(inAvsandare.getReferens().getValue());

			// Set ämne
			Amne inAmne = inRequest.getFKSKLTaEmotSvarAnrop().getAmne();
			outMeddelande.setAmne(transformAmneFromFK(inAmne));

			// Set fraga
			InnehallType fraga = new InnehallType();
			fraga.setMeddelandeText(inRequest.getFKSKLTaEmotSvarAnrop().getFraga().getText());
			fraga.setSigneringsTidpunkt(inRequest.getFKSKLTaEmotSvarAnrop().getFraga().getSignerades());
			outMeddelande.setFraga(fraga);

			// Set svar
			InnehallType svar = new InnehallType();
			svar.setMeddelandeText(inRequest.getFKSKLTaEmotSvarAnrop().getSvar().getText());
			svar.setSigneringsTidpunkt(inRequest.getFKSKLTaEmotSvarAnrop().getSvar().getSignerades());
			outMeddelande.setSvar(svar);

			// Vårdens referense id
			outMeddelande.setVardReferensId(inMottagare.getReferens().getValue());

			AttributedURIType logicalAddressHeader = new AttributedURIType();
			// Set new receiverid based on caregiver and careunit id
			String newReceiverId = inOrganisationMottagare.getId().getValue() + "#"
					+ inEnhetMottagare.getId().getValue();
			logicalAddressHeader.setValue(newReceiverId);
			// message.setProperty("receiverid", newReceiverId);

			Object[] payloadOut = new Object[] { logicalAddressHeader, outRequest };

			if (logger.isDebugEnabled()) {
				logger.debug("transformed payload to: " + payloadOut);
			}

			logger.info("Exiting fk2vard receive medical certificate answer transform");

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

	private Amnetyp transformAmneFromFK(Amne inAmne) {
		if (inAmne.getBeskrivning().startsWith("Arbe")) {
			return Amnetyp.ARBETSTIDSFORLAGGNING;
		} else if (inAmne.getBeskrivning().startsWith("Avst")) {
			return Amnetyp.AVSTAMNINGSMOTE;
		} else if (inAmne.getBeskrivning().equalsIgnoreCase("Komplettering")) {
			return Amnetyp.KOMPLETTERING_AV_LAKARINTYG;
		} else if (inAmne.getBeskrivning().equalsIgnoreCase("Kontakt")) {
			return Amnetyp.KONTAKT;
		} else if (inAmne.getBeskrivning().startsWith("P")) {
			return Amnetyp.PAMINNELSE;
		} else {
			return Amnetyp.OVRIGT;
		}
	}

}