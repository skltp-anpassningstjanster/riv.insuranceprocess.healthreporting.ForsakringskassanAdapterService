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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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

import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaType;
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
import se.fk.vardgivare.sjukvard.v1.TaEmotFraga;
import se.fk.vardgivare.sjukvard.v1.Telefon;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.VardAdresseringsType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.QuestionToFkType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionType;
import se.skl.riv.insuranceprocess.healthreporting.v2.EnhetType;
import se.skl.riv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.skl.riv.insuranceprocess.healthreporting.v2.PatientType;
import se.skl.riv.insuranceprocess.healthreporting.v2.VardgivareType;

public class VardRequest2FkTransformer extends AbstractMessageTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final JaxbUtil jaxbUtil = new JaxbUtil(SendMedicalCertificateQuestionType.class);

	public VardRequest2FkTransformer() {
		super();
		registerSourceType(DataTypeFactory.create(Object.class));
	}

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		logger.info("Entering vard2fk send medical certificate question transform");

		ResourceBundle rb = ResourceBundle.getBundle("fkdataSendMCQuestion");
		final String FK_ID = "2021005521";
		XMLStreamReader streamPayload = null;

		try {
			// Transform the XML payload into a JAXB object
			streamPayload = (XMLStreamReader) ((Object[]) message.getPayload())[1];
			SendMedicalCertificateQuestionType inRequest = (SendMedicalCertificateQuestionType) jaxbUtil
					.unmarshal(streamPayload);

			validateRequest(inRequest);

			// Get receiver to adress from Mule property
			// String receiverId = (String)message.getProperty("receiverid");

			// Create new JAXB object for the outgoing data
			TaEmotFragaType outRequest = new TaEmotFragaType();
			TaEmotFraga outTaEmotFraga = new TaEmotFraga();
			outRequest.setFKSKLTaEmotFragaAnrop(outTaEmotFraga);

			// Transform between incoming and outgoing objects
			// Avsändare - Vården
			VardAdresseringsType inAvsandare = inRequest.getQuestion().getAdressVard();
			HosPersonalType inHoSPersonalAvsandare = inAvsandare.getHosPersonal();
			EnhetType inEnhetAvsandare = inHoSPersonalAvsandare.getEnhet();
			VardgivareType inVardgivareAvsandare = inEnhetAvsandare.getVardgivare();

			Avsandare outAvsandare = new Avsandare();
			Adressering outAdressering = new Adressering();
			outAdressering.setAvsandare(outAvsandare);
			outTaEmotFraga.setAdressering(outAdressering);

			ReferensAdressering referensId = new ReferensAdressering();
			referensId.setValue(inRequest.getQuestion().getVardReferensId());
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
			} else {
				// Add dummy phonenumber to make FK systems happy!
				Telefon outEnhetTelefonAvsandare = new Telefon();
				outEnhetTelefonAvsandare.setValue("0000000");
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

			Namn outOrganisationNamnMottagare = new Namn();
			outOrganisationNamnMottagare.setValue(rb.getString("FK"));
			outOrganisationMottagare.setNamn(outOrganisationNamnMottagare);
			InternIdentitetsbeteckning outOrganisationIdMottagare = new InternIdentitetsbeteckning();
			outOrganisationIdMottagare.setValue("202100-5521");
			outOrganisationMottagare.setId(outOrganisationIdMottagare);

			// Skickades
			outAdressering.setSkickades(inRequest.getQuestion().getAvsantTidpunkt());

			// Patient
			LakarutlatandeEnkelType inLakarutlatande = inRequest.getQuestion().getLakarutlatande();
			PatientType inPatient = inLakarutlatande.getPatient();
			Patient outPatient = new Patient();
			outPatient.setIdentifierare(inPatient.getPersonId().getExtension());
			outPatient.setNamn(inPatient.getFullstandigtNamn());
			outTaEmotFraga.setPatient(outPatient);

			// Ämne
			Amnetyp inAmne = inRequest.getQuestion().getAmne();
			Amne outAmne = new Amne();
			outAmne.setBeskrivning(transformAmneFromVarden(inAmne, rb));
			outAmne.setFritext(transformAmneFromVarden(inAmne, rb));
			outTaEmotFraga.setAmne(outAmne);

			// Läkarintyg referens
			Lakarintygsreferens outLakarintyg = new Lakarintygsreferens();
			outLakarintyg.setReferens(inLakarutlatande.getLakarutlatandeId());
			outLakarintyg.setSignerades(inLakarutlatande.getSigneringsTidpunkt());
			outTaEmotFraga.setLakarintyg(outLakarintyg);

			// Fraga
			Meddelande fraga = new Meddelande();
			// Lägg till extra information om ämnet är en makulering
			if (inAmne.compareTo(Amnetyp.MAKULERING_AV_LAKARINTYG) == 0) {
				StringBuffer newFraga = new StringBuffer();
				newFraga.append("Intygs id: ");
				newFraga.append(inLakarutlatande.getLakarutlatandeId());
				newFraga.append("\n");
				newFraga.append("Signeringstidpunkt: ");
				newFraga.append(inLakarutlatande.getSigneringsTidpunkt().toString());
				newFraga.append(" Patientens namn: ");
				newFraga.append(inPatient.getFullstandigtNamn());
				newFraga.append("\n");
				newFraga.append("Orginal:");
				newFraga.append(inRequest.getQuestion().getFraga().getMeddelandeText());
				fraga.setText(newFraga.toString());
			} else {
				fraga.setText(inRequest.getQuestion().getFraga().getMeddelandeText());
			}
			fraga.setSignerades(inRequest.getQuestion().getFraga().getSigneringsTidpunkt());
			outTaEmotFraga.setFraga(fraga);

			AttributedURIType logicalAddressHeader = new AttributedURIType();
			logicalAddressHeader.setValue(FK_ID);

			Object[] payloadOut = new Object[] { logicalAddressHeader, outRequest };

			if (logger.isDebugEnabled()) {
				logger.debug("transformed payload to: " + payloadOut);
			}

			logger.info("Exiting vard2fk send medical certificate question transform");

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

	private void validateRequest(SendMedicalCertificateQuestionType parameters) throws Exception {
		// List of validation errors
		ArrayList<String> validationErrors = new ArrayList<String>();
		// Validate incoming request
		try {
			// Check that we got any data at all
			if (parameters == null) {
				validationErrors.add("No SendMedicalCertificateQuestion found in incoming data!");
				throw new Exception();
			}

			// Check that we got an question element
			if (parameters.getQuestion() == null) {
				validationErrors.add("No Question element found in incoming request data!");
				throw new Exception();
			}

			QuestionToFkType inQuestion = parameters.getQuestion();

			/**
			 * Check meddelande data + lakarutlatande reference
			 */

			// Meddelande id - mandatory
			if (inQuestion.getVardReferensId() == null || inQuestion.getVardReferensId().length() < 1) {
				validationErrors.add("No vardReferens-id found!");
			}

			// �mne - mandatory
			Amnetyp inAmne = inQuestion.getAmne();
			if (inAmne == null) {
				validationErrors.add("No Amne element found!");
			}

			/**
			 * Check that we got a question
			 */
			if (inQuestion.getFraga() == null) {
				validationErrors.add("No Question fraga element found!");
				throw new Exception();
			}
			if (inQuestion.getFraga().getMeddelandeText() == null
					|| inQuestion.getFraga().getMeddelandeText().length() < 1) {
				validationErrors.add("No Question fraga meddelandeText elements found or set!");
			}
			if (inQuestion.getFraga().getSigneringsTidpunkt() == null
					|| !inQuestion.getFraga().getSigneringsTidpunkt().isValid()) {
				validationErrors.add("No Question fraga signeringsTidpunkt elements found or set!");
			}

			// Avs�nt tidpunkt - mandatory
			if (inQuestion.getAvsantTidpunkt() == null || !inQuestion.getAvsantTidpunkt().isValid()) {
				validationErrors.add("No or wrong avsantTidpunkt found!");
			}

			// L�karutl�tande referens - mandatory
			if (inQuestion.getLakarutlatande() == null) {
				validationErrors.add("No lakarutlatande element found!");
				throw new Exception();
			}
			LakarutlatandeEnkelType inLakarUtlatande = inQuestion.getLakarutlatande();

			// L�karutl�tande referens - id - mandatory
			if (inLakarUtlatande.getLakarutlatandeId() == null || inLakarUtlatande.getLakarutlatandeId().length() < 1) {
				validationErrors.add("No lakarutlatande-id found!");
			}

			// L�karutl�tande referens - signeringsTidpunkt - mandatory
			if (inLakarUtlatande.getSigneringsTidpunkt() == null || !inLakarUtlatande.getSigneringsTidpunkt().isValid()) {
				validationErrors.add("No or wrong lakarutlatande-signeringsTidpunkt found!");
			}

			// L�karutl�tande referens - patient - mandatory
			if (inLakarUtlatande.getPatient() == null) {
				validationErrors.add("No lakarutlatande patient element found!");
				throw new Exception();
			}
			PatientType inPatient = inLakarUtlatande.getPatient();

			// L�karutl�tande referens - patient - personid mandatory
			// Check patient id - mandatory
			if (inPatient.getPersonId() == null || inPatient.getPersonId().getExtension() == null
					|| inPatient.getPersonId().getExtension().length() < 1) {
				validationErrors.add("No lakarutlatande-Patient Id found!");
			}

			// Check patient o.i.d.
			if (inPatient.getPersonId() == null
					|| inPatient.getPersonId().getRoot() == null
					|| (!inPatient.getPersonId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.3.1") && !inPatient
							.getPersonId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.3.3"))) {
				validationErrors
						.add("Wrong o.i.d. for Patient Id! Should be 1.2.752.129.2.1.3.1 or 1.2.752.129.2.1.3.3");
			}
			String inPersonnummer = inPatient.getPersonId().getExtension();

			// Check format of patient id - personnummer valid format is
			// 19121212-1212 or 19121212+1212
//			if (!Pattern.matches("[0-9]{8}[-+][0-9]{4}", inPersonnummer)) {
//				validationErrors.add("Wrong format for person-id! Valid format is YYYYMMDD-XXXX or YYYYMMDD+XXXX.");
//			}

			// L�karutl�tande referens - patient - namn - mandatory
			if (inPatient.getFullstandigtNamn() == null || inPatient.getFullstandigtNamn().length() < 1) {
				validationErrors.add("No lakarutlatande Patient fullstandigtNamn elements found or set!");
			}

			/**
			 * Check avs�ndar data.
			 */
			if (inQuestion.getAdressVard() == null) {
				validationErrors.add("No adressVard element found!");
				throw new Exception();
			}
			if (inQuestion.getAdressVard().getHosPersonal() == null) {
				validationErrors.add("No adressVard - hosPersonal element found!");
				throw new Exception();
			}
			HosPersonalType inHoSP = inQuestion.getAdressVard().getHosPersonal();

			// Check lakar id - mandatory
			if (inHoSP.getPersonalId() == null || inHoSP.getPersonalId().getExtension() == null
					|| inHoSP.getPersonalId().getExtension().length() < 1) {
				validationErrors.add("No personal-id found!");
			}
			// Check lakar id o.i.d.
			if (inHoSP.getPersonalId() == null || inHoSP.getPersonalId().getRoot() == null
					|| !inHoSP.getPersonalId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
				validationErrors.add("Wrong o.i.d. for personalId! Should be 1.2.752.129.2.1.4.1");
			}

			// Check lakarnamn - mandatory
			if (inHoSP.getFullstandigtNamn() == null || inHoSP.getFullstandigtNamn().length() < 1) {
				validationErrors.add("No skapadAvHosPersonal fullstandigtNamn elements found or set!");
			}

			// Check that we got a enhet element
			if (inHoSP.getEnhet() == null) {
				validationErrors.add("No enhet element found!");
				throw new Exception();
			}
			EnhetType inEnhet = inHoSP.getEnhet();

			// Check enhets id - mandatory
			if (inEnhet.getEnhetsId() == null || inEnhet.getEnhetsId().getExtension() == null
					|| inEnhet.getEnhetsId().getExtension().length() < 1) {
				validationErrors.add("No enhets-id found!");
			}
			// Check enhets o.i.d
			if (inEnhet.getEnhetsId() == null || inEnhet.getEnhetsId().getRoot() == null
					|| !inEnhet.getEnhetsId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
				validationErrors.add("Wrong o.i.d. for enhetsId! Should be 1.2.752.129.2.1.4.1");
			}

			// Check enhetsnamn - mandatory
			if (inEnhet.getEnhetsnamn() == null || inEnhet.getEnhetsnamn().length() < 1) {
				validationErrors.add("No enhetsnamn found!");
			}

			// Check that we got a vardgivare element
			if (inEnhet.getVardgivare() == null) {
				validationErrors.add("No vardgivare element found!");
				throw new Exception();
			}
			VardgivareType inVardgivare = inEnhet.getVardgivare();

			// Check vardgivare id - mandatory
			if (inVardgivare.getVardgivareId() == null || inVardgivare.getVardgivareId().getExtension() == null
					|| inVardgivare.getVardgivareId().getExtension().length() < 1) {
				validationErrors.add("No vardgivare-id found!");
			}
			// Check vardgivare o.i.d.
			if (inVardgivare.getVardgivareId() == null || inVardgivare.getVardgivareId().getRoot() == null
					|| !inVardgivare.getVardgivareId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
				validationErrors.add("Wrong o.i.d. for vardgivareId! Should be 1.2.752.129.2.1.4.1");
			}

			// Check vardgivarename - mandatory
			if (inVardgivare.getVardgivarnamn() == null || inVardgivare.getVardgivarnamn().length() < 1) {
				validationErrors.add("No vardgivarenamn found!");
			}

			// Check if we got any validation errors that not caused an
			// Exception
			if (validationErrors.size() > 0) {
				throw new Exception();
			}

			// Check if we got any validation errors that not caused an
			// Exception
			if (validationErrors.size() > 0) {
				logger.error("Validate exception:" + getValidationErrors(validationErrors));
				throw new Exception();
			}

			// No validation errors!
		} catch (Exception e) {
			throw new Exception(getValidationErrors(validationErrors));
		}
	}

	private String getValidationErrors(ArrayList<String> validationErrors) {
		int i = 1;
		StringBuffer validationString = new StringBuffer();
		Iterator<String> iterValidationErrors = validationErrors.iterator();
		validationString.append("Validation error " + i++ + ":");
		validationString.append((String) iterValidationErrors.next());
		while (iterValidationErrors.hasNext()) {
			validationString.append("\n\rValidation error " + i++ + ":");
			validationString.append((String) iterValidationErrors.next());
		}
		return validationString.toString();
	}
}