package se.skl.skltpservices.adapter.fk.sendmedcertanswer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageAwareTransformer;
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
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.VardAdresseringsType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.AnswerToFkType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.SendMedicalCertificateAnswerType;
import se.skl.riv.insuranceprocess.healthreporting.v2.EnhetType;
import se.skl.riv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.skl.riv.insuranceprocess.healthreporting.v2.PatientType;
import se.skl.riv.insuranceprocess.healthreporting.v2.VardgivareType;

public class VardRequest2FkTransformer extends AbstractMessageAwareTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final JaxbUtil jaxbUtil = new JaxbUtil(SendMedicalCertificateAnswerType.class);

	public VardRequest2FkTransformer() {
		super();
		registerSourceType(Object.class);
		setReturnClass(Object.class);
	}

	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {

		logger.info("Entering vard2fk send medical certificate answer transform");

		ResourceBundle rb = ResourceBundle.getBundle("fkdataSendMCAnswer");
		final String FK_ID = "2021005521";
		XMLStreamReader streamPayload = null;

		try {
			// Transform the XML payload into a JAXB object
			streamPayload = (XMLStreamReader) ((Object[]) message.getPayload())[1];
			SendMedicalCertificateAnswerType inRequest = (SendMedicalCertificateAnswerType) jaxbUtil
					.unmarshal(streamPayload);

			validateRequest(inRequest);

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

	private void validateRequest(SendMedicalCertificateAnswerType parameters) throws Exception {
		// List of validation errors
		ArrayList<String> validationErrors = new ArrayList<String>();
		// Validate incoming request
		try {
			// Check that we got any data at all
			if (parameters == null) {
				validationErrors.add("No SendMedicalCertificateAnswer found in incoming data!");
				throw new Exception();
			}

			// Check that we got an answer element
			if (parameters.getAnswer() == null) {
				validationErrors.add("No Answer element found in incoming request data!");
				throw new Exception();
			}

			AnswerToFkType inAnswer = parameters.getAnswer();

			/**
			 * Check meddelande data + lakarutlatande reference
			 */

			// Meddelande id vården - mandatory
			if (inAnswer.getVardReferensId() == null || inAnswer.getVardReferensId().length() < 1) {
				validationErrors.add("No vardReferens-id found!");
			}

			// Meddelande id FK - mandatory
			if (inAnswer.getFkReferensId() == null || inAnswer.getFkReferensId().length() < 1) {
				validationErrors.add("No fkReferens-id found!");
			}

			// Ämne - mandatory
			Amnetyp inAmne = inAnswer.getAmne();
			if (inAmne == null) {
				validationErrors.add("No Amne element found!");
			}

			/**
			 * Check that we got a question
			 */
			if (inAnswer.getFraga() == null) {
				validationErrors.add("No Answer fraga element found!");
				throw new Exception();
			}
			if (inAnswer.getFraga().getMeddelandeText() == null || inAnswer.getFraga().getMeddelandeText().length() < 1) {
				validationErrors.add("No Answer fraga meddelandeText elements found or set!");
			}
			if (inAnswer.getFraga().getSigneringsTidpunkt() == null
					|| !inAnswer.getFraga().getSigneringsTidpunkt().isValid()) {
				validationErrors.add("No Answer fraga signeringsTidpunkt elements found or set!");
			}

			/**
			 * Check that we got an answer
			 */
			if (inAnswer.getSvar() == null) {
				validationErrors.add("No Answer svar element found!");
				throw new Exception();
			}
			if (inAnswer.getSvar().getMeddelandeText() == null || inAnswer.getSvar().getMeddelandeText().length() < 1) {
				validationErrors.add("No Answer svar meddelandeText elements found or set!");
			}
			if (inAnswer.getSvar().getSigneringsTidpunkt() == null
					|| !inAnswer.getSvar().getSigneringsTidpunkt().isValid()) {
				validationErrors.add("No Answer svar signeringsTidpunkt elements found or set!");
			}

			// Avsänt tidpunkt - mandatory
			if (inAnswer.getAvsantTidpunkt() == null || !inAnswer.getAvsantTidpunkt().isValid()) {
				validationErrors.add("No or wrong avsantTidpunkt found!");
			}

			// Läkarutlåtande referens - mandatory
			if (inAnswer.getLakarutlatande() == null) {
				validationErrors.add("No lakarutlatande element found!");
				throw new Exception();
			}
			LakarutlatandeEnkelType inLakarUtlatande = inAnswer.getLakarutlatande();

			// Läkarutlåtande referens - id - mandatory
			if (inLakarUtlatande.getLakarutlatandeId() == null || inLakarUtlatande.getLakarutlatandeId().length() < 1) {
				validationErrors.add("No lakarutlatande-id found!");
			}

			// Läkarutlåtande referens - signeringsTidpunkt - mandatory
			if (inLakarUtlatande.getSigneringsTidpunkt() == null || !inLakarUtlatande.getSigneringsTidpunkt().isValid()) {
				validationErrors.add("No or wrong lakarutlatande-signeringsTidpunkt found!");
			}

			// Läkarutlåtande referens - patient - mandatory
			if (inLakarUtlatande.getPatient() == null) {
				validationErrors.add("No lakarutlatande patient element found!");
				throw new Exception();
			}
			PatientType inPatient = inLakarUtlatande.getPatient();

			// Läkarutlåtande referens - patient - personid mandatory
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
//			if (!Pattern.matches("[0-9]{8}[-+][0-9]{4}", inPersonnummer) ) {
//				validationErrors.add("Wrong format for person-id! Valid format is YYYYMMDD-XXXX or YYYYMMDD+XXXX.");
//			}

			// Läkarutlåtande referens - patient - namn - mandatory
			if (inPatient.getFullstandigtNamn() == null || inPatient.getFullstandigtNamn().length() < 1) {
				validationErrors.add("No lakarutlatande Patient fullstandigtNamn elements found or set!");
			}

			/**
			 * Check avsändar data.
			 */
			if (inAnswer.getAdressVard() == null) {
				validationErrors.add("No adressVard element found!");
				throw new Exception();
			}
			if (inAnswer.getAdressVard().getHosPersonal() == null) {
				validationErrors.add("No adressVard - hosPersonal element found!");
				throw new Exception();
			}
			HosPersonalType inHoSP = inAnswer.getAdressVard().getHosPersonal();

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