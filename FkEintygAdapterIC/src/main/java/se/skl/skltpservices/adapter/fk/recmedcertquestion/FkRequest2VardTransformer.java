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
package se.skl.skltpservices.adapter.fk.recmedcertquestion;

import iso.v21090.dt.v1.II;

import java.util.List;

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

import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaType;
import se.fk.vardgivare.sjukvard.v1.Adressering.Avsandare;
import se.fk.vardgivare.sjukvard.v1.Adressering.Mottagare;
import se.fk.vardgivare.sjukvard.v1.Amne;
import se.fk.vardgivare.sjukvard.v1.Enhet;
import se.fk.vardgivare.sjukvard.v1.Falt;
import se.fk.vardgivare.sjukvard.v1.Lakarintygsreferens;
import se.fk.vardgivare.sjukvard.v1.Organisation;
import se.fk.vardgivare.sjukvard.v1.Patient;
import se.fk.vardgivare.sjukvard.v1.Person;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.Amnetyp;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.FkKontaktType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.InnehallType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.KompletteringType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.VardAdresseringsType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestionresponder.v1.QuestionFromFkType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestionresponder.v1.ReceiveMedicalCertificateQuestionType;
import se.skl.riv.insuranceprocess.healthreporting.v2.EnhetType;
import se.skl.riv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.skl.riv.insuranceprocess.healthreporting.v2.PatientType;
import se.skl.riv.insuranceprocess.healthreporting.v2.VardgivareType;

public class FkRequest2VardTransformer extends AbstractMessageTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final JaxbUtil jaxbUtil = new JaxbUtil(TaEmotFragaType.class);

	// private Pattern pattern;
	// private String senderIdPropertyName;

	// public void setSenderIdPropertyName(String senderIdPropertyName) {
	// this.senderIdPropertyName = senderIdPropertyName;
	// pattern = Pattern.compile(this.senderIdPropertyName + "=([^,]+)");
	// if (logger.isInfoEnabled()) {
	// logger.info("senderIdPropertyName set to: " + senderIdPropertyName);
	// }
	// }

	public FkRequest2VardTransformer() {
		super();
		registerSourceType(DataTypeFactory.create(Object.class));
	}

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
	
		XMLStreamReader streamPayload = null;

		logger.info("Entering fk2vard receive medical certificate question transform");

		// Check caller certificate ID!
		// String callerCertificateId = getSenderIdFromCertificate(message,
		// pattern);

		try {
			// Transform the XML payload into a JAXB object
			streamPayload = (XMLStreamReader) ((Object[]) message.getPayload())[1];
			TaEmotFragaType inRequest = (TaEmotFragaType) jaxbUtil.unmarshal(streamPayload);

			// Create new JAXB object for the outgoing data
			ReceiveMedicalCertificateQuestionType outRequest = new ReceiveMedicalCertificateQuestionType();
			QuestionFromFkType outMeddelande = new QuestionFromFkType();
			outRequest.setQuestion(outMeddelande);

			// Transform between incoming and outgoing objects
			// Avsändare - FK, Create contact info from FK as separate strings
			Avsandare inAvsandare = inRequest.getFKSKLTaEmotFragaAnrop().getAdressering().getAvsandare();
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
			Mottagare inMottagare = inRequest.getFKSKLTaEmotFragaAnrop().getAdressering().getMottagare();
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
						&& inEnhetMottagare.getKontaktuppgifter().getTelefon().getValue().length() > 0
						&& !inEnhetMottagare.getKontaktuppgifter().getTelefon().getValue().equalsIgnoreCase("0000000")) { // Dummy
																															// value
																															// added
																															// in
																															// communication
																															// to
																															// FK
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
			XMLGregorianCalendar inSkickades = inRequest.getFKSKLTaEmotFragaAnrop().getAdressering().getSkickades();
			outMeddelande.setAvsantTidpunkt(inSkickades);

			// Set läkarutlåtande enkel från vården
			Lakarintygsreferens inLakarutlatande = inRequest.getFKSKLTaEmotFragaAnrop().getLakarintyg();
			Patient inPatient = inRequest.getFKSKLTaEmotFragaAnrop().getPatient();

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
			Amne inAmne = inRequest.getFKSKLTaEmotFragaAnrop().getAmne();
			outMeddelande.setAmne(transformAmneFromFK(inAmne));

			// Set meddelande rubrik och text
			if (inAmne.getFritext() != null && inAmne.getFritext().length() > 0) {
				outMeddelande.setFkMeddelanderubrik(inAmne.getFritext());
			}

			// Set fraga
			InnehallType fraga = new InnehallType();
			fraga.setMeddelandeText(inRequest.getFKSKLTaEmotFragaAnrop().getFraga().getText());
			fraga.setSigneringsTidpunkt(inRequest.getFKSKLTaEmotFragaAnrop().getFraga().getSignerades());
			outMeddelande.setFraga(fraga);

			// Komplettering - enbart om ämne är komplettering
			if (outMeddelande.getAmne().compareTo(Amnetyp.KOMPLETTERING_AV_LAKARINTYG) == 0) {
				List<Falt> inKompletteringar = inLakarutlatande.getFalt();
				for (int i = 0; i < inKompletteringar.size(); i++) {
					Falt inTempFalt = inKompletteringar.get(i);
					KompletteringType outKomplettering = new KompletteringType();
					outKomplettering.setFalt(inTempFalt.getNamn());
					if (inTempFalt.getKommentar() != null && inTempFalt.getKommentar().length() > 0) {
						outKomplettering.setText(inTempFalt.getKommentar());
					} else {
						outKomplettering.setText("<Ingen text satt>");
					}
					outMeddelande.getFkKomplettering().add(outKomplettering);
				}
			}

			// Sista datum för komplettering
			if (inRequest.getFKSKLTaEmotFragaAnrop().getBesvaras() != null) {
				outMeddelande.setFkSistaDatumForSvar(inRequest.getFKSKLTaEmotFragaAnrop().getBesvaras());
			}

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

			logger.info("Exiting fk2vard receive medical certificate question transform");

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

	// private String getSenderIdFromCertificate(MuleMessage message, final
	// Pattern pattern) {
	// String senderId = null;
	// Certificate[] peerCertificateChain = (Certificate[]) message
	// .getProperty("PEER_CERTIFICATES");
	//
	// if (peerCertificateChain != null) {
	// // Check type of first certificate in the chain, this should be the
	// // clients certificate
	// if (peerCertificateChain[0] instanceof X509Certificate) {
	// X509Certificate cert = (X509Certificate) peerCertificateChain[0];
	// String principalName = cert.getSubjectX500Principal().getName();
	// Matcher matcher = pattern.matcher(principalName);
	// if (matcher.find()) {
	// senderId = matcher.group(1);
	// } else {
	// String errorMessage = ("VP002 No senderId found in Certificate: " +
	// principalName);
	// logger.info(errorMessage);
	// // throw new VpSemanticException(errorMessage);
	//
	// }
	// } else {
	// String errorMessage =
	// ("VP002 No senderId found in Certificate: First certificate in chain is not X509Certificate: "
	// + peerCertificateChain[0]);
	// logger.info(errorMessage);
	// // throw new VpSemanticException(errorMessage);
	// }
	// } else {
	// String errorMessage =
	// ("VP002 No senderId found in Certificate: No certificate chain found from client");
	// logger.info(errorMessage);
	// // throw new VpSemanticException(errorMessage);
	// }
	//
	// // Check if this is coded in hex (HCC Funktionscertifikat does that!)
	// if (senderId.startsWith("#")) {
	// return convertFromHexToString(senderId.substring(5));
	// } else {
	// return senderId;
	// }
	// }
	//
	// private String convertFromHexToString(final String hexString) {
	// byte [] txtInByte = new byte [hexString.length() / 2];
	// int j = 0;
	// for (int i = 0; i < hexString.length(); i += 2)
	// {
	// txtInByte[j++] = Byte.parseByte(hexString.substring(i, i + 2), 16);
	// }
	// return new String(txtInByte);
	// }

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