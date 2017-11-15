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
package se.skl.skltpservices.adapter.fk.regmedcert;

import static se.skl.skltpservices.adapter.fk.regmedcert.RegisterMedicalCertificateUtil.findAktivitetWithCode;
import static se.skl.skltpservices.adapter.fk.regmedcert.RegisterMedicalCertificateUtil.findArbetsformaga;
import static se.skl.skltpservices.adapter.fk.regmedcert.RegisterMedicalCertificateUtil.findFunktionsTillstandType;
import static se.skl.skltpservices.adapter.fk.regmedcert.RegisterMedicalCertificateUtil.findReferensTyp;
import static se.skl.skltpservices.adapter.fk.regmedcert.RegisterMedicalCertificateUtil.findTypAvSysselsattning;
import static se.skl.skltpservices.adapter.fk.regmedcert.RegisterMedicalCertificateUtil.findVardkontaktTyp;

import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.joda.time.LocalDateTime;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.w3.wsaddressing10.AttributedURIType;

import se.fk.vardgivare.sjukvard.taemotlakarintygresponder.v1.TaEmotLakarintygType;
import se.fk.vardgivare.sjukvard.v1.Adress;
import se.fk.vardgivare.sjukvard.v1.Adressering;
import se.fk.vardgivare.sjukvard.v1.Adressering.Avsandare;
import se.fk.vardgivare.sjukvard.v1.Adressering.Mottagare;
import se.fk.vardgivare.sjukvard.v1.Enhet;
import se.fk.vardgivare.sjukvard.v1.Epostadress;
import se.fk.vardgivare.sjukvard.v1.InternIdentitetsbeteckning;
import se.fk.vardgivare.sjukvard.v1.Kontaktuppgifter;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Arbetsformaga;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Arbetsformaga.Nedsattningsgrad;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Begransning;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Diagnos;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Kontakt;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Lakare;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Motivering;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Planering;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Planering.Behandling;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Prognos;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Rehabilitering;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Rekommendationer;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Rekommendationer.Rekommendation;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Resor;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Sjukdomshistoria;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Smittskydd;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Status;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Status.Basering;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Status.Basering.Ursprung;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Sysselsattning;
import se.fk.vardgivare.sjukvard.v1.Lakarintyg.Upplysningar;
import se.fk.vardgivare.sjukvard.v1.Land;
import se.fk.vardgivare.sjukvard.v1.Namn;
import se.fk.vardgivare.sjukvard.v1.NationellIdentitetsbeteckning;
import se.fk.vardgivare.sjukvard.v1.Organisation;
import se.fk.vardgivare.sjukvard.v1.Patient;
import se.fk.vardgivare.sjukvard.v1.Person;
import se.fk.vardgivare.sjukvard.v1.Postadress;
import se.fk.vardgivare.sjukvard.v1.Postnummer;
import se.fk.vardgivare.sjukvard.v1.Postort;
import se.fk.vardgivare.sjukvard.v1.ReferensAdressering;
import se.fk.vardgivare.sjukvard.v1.TaEmotLakarintyg;
import se.fk.vardgivare.sjukvard.v1.Telefon;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.AktivitetType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Aktivitetskod;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.ArbetsformagaNedsattningType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.FunktionstillstandType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Prognosangivelse;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.ReferensType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Referenstyp;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.SysselsattningType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.TypAvFunktionstillstand;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.TypAvSysselsattning;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.VardkontaktType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Vardkontakttyp;
import se.inera.ifv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v3.RegisterMedicalCertificateType;

public class Vard2FkTransformer extends AbstractMessageTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final JaxbUtil jaxbUtil = new JaxbUtil(RegisterMedicalCertificateType.class);
	private Vard2FkValidator validator = new Vard2FkValidator();
	
	public Vard2FkTransformer() {
		super();
		registerSourceType(DataTypeFactory.create(Object.class));
	}

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		logger.info("Entering vard2fk register medical certificate transform");

		ResourceBundle rb = ResourceBundle.getBundle("fkdataRegMedCert");
		final String FK_ID = "2021005521";
		XMLStreamReader streamPayload = null;

		try {
			// Transform the XML payload into a JAXB object
			streamPayload = (XMLStreamReader) ((Object[]) message.getPayload())[1];
			RegisterMedicalCertificateType inRequest = (RegisterMedicalCertificateType) jaxbUtil
					.unmarshal(streamPayload);

			// Get receiver to adress from Mule property
			// String receiverId = (String)message.getProperty("receiverid");

			// Validate incoming request
			validator.validateRequest(inRequest);

			// Extract all incoming data to local variables
			String emuId = inRequest.getLakarutlatande().getLakarutlatandeId();
			String inPersonnummer = inRequest.getLakarutlatande().getPatient().getPersonId().getExtension();
			String inPatientNamn = inRequest.getLakarutlatande().getPatient().getFullstandigtNamn();
			String inLakarId = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getPersonalId().getExtension();
			String inLakarNamn = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getFullstandigtNamn();
			String inLakarForskrivarekod = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getForskrivarkod();
			String inEnhetsId = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getEnhetsId()
					.getExtension();
			String inEnhetsNamn = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getEnhetsnamn();
			String inEnhetsArbetsplatskod = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet()
					.getArbetsplatskod().getExtension();
			String inEnhetsPostAdress = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet()
					.getPostadress();
			String inEnhetsPostNummer = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet()
					.getPostnummer();
			String inEnhetsPostOrt = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getPostort();
			String inEnhetsTelefonNummer = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet()
					.getTelefonnummer();
			String inEnhetsEpost = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getEpost();
			String inVardgivareId = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare()
					.getVardgivareId().getExtension();
			String inVardgivarNamn = inRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare()
					.getVardgivarnamn();
			LocalDateTime inSignerades = inRequest.getLakarutlatande().getSigneringsdatum();
			LocalDateTime inSkickadesTid = inRequest.getLakarutlatande().getSkickatDatum();
			boolean inSmittskydd = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.AVSTANGNING_ENLIGT_SM_L_PGA_SMITTA) != null ? true : false;
			String inDiagnoskod = "";
			if (inRequest.getLakarutlatande().getMedicinsktTillstand() != null
					&& inRequest.getLakarutlatande().getMedicinsktTillstand().getTillstandskod() != null) {
				inDiagnoskod = inRequest.getLakarutlatande().getMedicinsktTillstand().getTillstandskod().getCode();
			}
			String inDiagnosBeskrivning = "";
			if (inRequest.getLakarutlatande().getMedicinsktTillstand() != null) {
				inDiagnosBeskrivning = inRequest.getLakarutlatande().getMedicinsktTillstand().getBeskrivning();
			}
			String inSjukdomshistoriaBeskrivning = "";
			if (inRequest.getLakarutlatande().getBedomtTillstand() != null) {
				inSjukdomshistoriaBeskrivning = inRequest.getLakarutlatande().getBedomtTillstand().getBeskrivning();
			}
			FunktionstillstandType inKroppsFunktion = findFunktionsTillstandType(inRequest.getLakarutlatande()
					.getFunktionstillstand(), TypAvFunktionstillstand.KROPPSFUNKTION);
			VardkontaktType inUndersokning = findVardkontaktTyp(inRequest.getLakarutlatande().getVardkontakt(),
					Vardkontakttyp.MIN_UNDERSOKNING_AV_PATIENTEN);
			VardkontaktType inTelefonkontakt = findVardkontaktTyp(inRequest.getLakarutlatande().getVardkontakt(),
					Vardkontakttyp.MIN_TELEFONKONTAKT_MED_PATIENTEN);
			ReferensType inJournal = findReferensTyp(inRequest.getLakarutlatande().getReferens(),
					Referenstyp.JOURNALUPPGIFTER);
			ReferensType inAnnat = findReferensTyp(inRequest.getLakarutlatande().getReferens(), Referenstyp.ANNAT);
			FunktionstillstandType inAktivitetFunktion = findFunktionsTillstandType(inRequest.getLakarutlatande()
					.getFunktionstillstand(), TypAvFunktionstillstand.AKTIVITET);
			AktivitetType kontaktAF = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.PATIENTEN_BEHOVER_FA_KONTAKT_MED_ARBETSFORMEDLINGEN);
			AktivitetType kontaktFHV = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.PATIENTEN_BEHOVER_FA_KONTAKT_MED_FORETAGSHALSOVARDEN);
			AktivitetType ovrigt = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.OVRIGT);
			AktivitetType planeradAtgardInomSjukvarden = findAktivitetWithCode(inRequest.getLakarutlatande()
					.getAktivitet(), Aktivitetskod.PLANERAD_ELLER_PAGAENDE_BEHANDLING_ELLER_ATGARD_INOM_SJUKVARDEN);
			AktivitetType planeradAtgardAnnan = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.PLANERAD_ELLER_PAGAENDE_ANNAN_ATGARD);
			AktivitetType arbRelRehabAktuell = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.ARBETSLIVSINRIKTAD_REHABILITERING_AR_AKTUELL);
			AktivitetType arbRelRehabEjAktuell = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.ARBETSLIVSINRIKTAD_REHABILITERING_AR_EJ_AKTUELL);
			AktivitetType garEjAttBedommaArbRelRehab = findAktivitetWithCode(inRequest.getLakarutlatande()
					.getAktivitet(), Aktivitetskod.GAR_EJ_ATT_BEDOMMA_OM_ARBETSLIVSINRIKTAD_REHABILITERING_AR_AKTUELL);
			SysselsattningType inArbete = null;
			SysselsattningType inArbetslos = null;
			SysselsattningType inForaldraledig = null;
			if (inAktivitetFunktion.getArbetsformaga().getSysselsattning() != null) {
				inArbete = findTypAvSysselsattning(inAktivitetFunktion.getArbetsformaga().getSysselsattning(),
						TypAvSysselsattning.NUVARANDE_ARBETE);
				inArbetslos = findTypAvSysselsattning(inAktivitetFunktion.getArbetsformaga().getSysselsattning(),
						TypAvSysselsattning.ARBETSLOSHET);
				inForaldraledig = findTypAvSysselsattning(inAktivitetFunktion.getArbetsformaga().getSysselsattning(),
						TypAvSysselsattning.FORALDRALEDIGHET);
			}
			ArbetsformagaNedsattningType nedsatt14del = findArbetsformaga(inAktivitetFunktion.getArbetsformaga()
					.getArbetsformagaNedsattning(),
					se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad.NEDSATT_MED_1_4);
			ArbetsformagaNedsattningType nedsatthalften = findArbetsformaga(inAktivitetFunktion.getArbetsformaga()
					.getArbetsformagaNedsattning(),
					se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad.NEDSATT_MED_1_2);
			ArbetsformagaNedsattningType nedsatt34delar = findArbetsformaga(inAktivitetFunktion.getArbetsformaga()
					.getArbetsformagaNedsattning(),
					se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad.NEDSATT_MED_3_4);
			ArbetsformagaNedsattningType heltNedsatt = findArbetsformaga(inAktivitetFunktion.getArbetsformaga()
					.getArbetsformagaNedsattning(),
					se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad.HELT_NEDSATT);
			String inMotivering = "";
			if (inAktivitetFunktion.getArbetsformaga() != null) {
				inMotivering = inAktivitetFunktion.getArbetsformaga().getMotivering();
			}
			boolean inPrognosAterfaHelt = false;
			boolean inPrognosAterfaDelvis = false;
			boolean inPrognosEjAterfa = false;
			boolean inPrognosGarEjAttBedomma = false;
			if (inAktivitetFunktion.getArbetsformaga().getPrognosangivelse() != null) {
				inPrognosAterfaHelt = inAktivitetFunktion.getArbetsformaga().getPrognosangivelse()
						.compareTo(Prognosangivelse.ATERSTALLAS_HELT) == 0;
				inPrognosAterfaDelvis = inAktivitetFunktion.getArbetsformaga().getPrognosangivelse()
						.compareTo(Prognosangivelse.ATERSTALLAS_DELVIS) == 0;
				inPrognosEjAterfa = inAktivitetFunktion.getArbetsformaga().getPrognosangivelse()
						.compareTo(Prognosangivelse.INTE_ATERSTALLAS) == 0;
				inPrognosGarEjAttBedomma = inAktivitetFunktion.getArbetsformaga().getPrognosangivelse()
						.compareTo(Prognosangivelse.DET_GAR_INTE_ATT_BEDOMMA) == 0;
			}
			boolean inResorJa = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.FORANDRAT_RESSATT_TILL_ARBETSPLATSEN_AR_AKTUELLT) != null;
			boolean inResorNej = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.FORANDRAT_RESSATT_TILL_ARBETSPLATSEN_AR_EJ_AKTUELLT) != null;
			boolean inKontaktFK = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.KONTAKT_MED_FORSAKRINGSKASSAN_AR_AKTUELL) != null;
			String inKommentar = inRequest.getLakarutlatande().getKommentar();

			// Create new JAXB object for the outgoing data
			TaEmotLakarintygType outRequest = new TaEmotLakarintygType();
			TaEmotLakarintyg fkSklTELA = new TaEmotLakarintyg();
			outRequest.setFKSKLTaEmotLakarintygAnrop(fkSklTELA);

			// Transform between incoming and outgoing objects
			Adressering adressering = new Adressering();
			Avsandare avsandare = new Avsandare();
			Mottagare mottagare = new Mottagare();
			Organisation avsandarOrganisation = new Organisation();
			Enhet enhet = new Enhet();
			Lakarintyg lakarintyg = new Lakarintyg();

			/********* Avsändare *******/
			// Id
			ReferensAdressering referens = new ReferensAdressering();
			referens.setValue(emuId);
			avsandare.setReferens(referens);
			adressering.setAvsandare(avsandare);

			// Patient
			Patient patient = new Patient();
			patient.setIdentifierare(inPersonnummer);
			patient.setNamn(inPatientNamn);
			fkSklTELA.setPatient(patient);

			// Personal
			Person personal = new Person();
			InternIdentitetsbeteckning iiId = new InternIdentitetsbeteckning();
			iiId.setValue(inLakarId);
			personal.setId(iiId);
			personal.setNamn(inLakarNamn);
			enhet.setPerson(personal);

			// Enhet
			InternIdentitetsbeteckning iiEnhetId = new InternIdentitetsbeteckning();
			iiEnhetId.setValue(inEnhetsId);
			enhet.setId(iiEnhetId);
			Namn enhetsNamn = new Namn();
			enhetsNamn.setValue(inEnhetsNamn);
			enhet.setNamn(enhetsNamn);
			Kontaktuppgifter enhetKontaktuppgift = new Kontaktuppgifter();
			Adress enhetAdress = new Adress();
			Postadress postAdress = new Postadress();
			postAdress.setValue(inEnhetsPostAdress);
			enhetAdress.setPostadress(postAdress);
			Postnummer postnummer = new Postnummer();
			postnummer.setValue(inEnhetsPostNummer);
			enhetAdress.setPostnummer(postnummer);
			Postort postort = new Postort();
			postort.setValue(inEnhetsPostOrt);
			enhetAdress.setPostort(postort);
			Land land = new Land();
			land.setValue("Sverige");
			enhetAdress.setLand(land);
			enhetKontaktuppgift.setAdress(enhetAdress);
			Telefon telefon = new Telefon();
			telefon.setValue(inEnhetsTelefonNummer);
			enhetKontaktuppgift.setTelefon(telefon);
			if (inEnhetsEpost != null && inEnhetsEpost.length() > 0) {
				Epostadress epost = new Epostadress();
				epost.setValue(inEnhetsEpost);
				enhetKontaktuppgift.setEpost(epost);
			}
			enhet.setKontaktuppgifter(enhetKontaktuppgift);
			avsandarOrganisation.setEnhet(enhet);

			// Organisation
			InternIdentitetsbeteckning iiOrganisationsId = new InternIdentitetsbeteckning();
			iiOrganisationsId.setValue(inVardgivareId);
			avsandarOrganisation.setId(iiOrganisationsId);
			Namn organisationsNamn = new Namn();
			organisationsNamn.setValue(inVardgivarNamn);
			avsandarOrganisation.setNamn(organisationsNamn);
			avsandare.setOrganisation(avsandarOrganisation);
			adressering.setAvsandare(avsandare);
			fkSklTELA.setAdressering(adressering);

			/********* Mottagare *******/
			// Organisation
			Organisation mottagarOrganisation = new Organisation();
			NationellIdentitetsbeteckning mottagarNationellIdentitetsbeteckning = new NationellIdentitetsbeteckning();
			mottagarNationellIdentitetsbeteckning.setValue("202100-5521");
			mottagarOrganisation.setOrganisationsnummer(mottagarNationellIdentitetsbeteckning);
			Namn mottagarNamn = new Namn();
			mottagarNamn.setValue(rb.getString("FK"));
			mottagarOrganisation.setNamn(mottagarNamn);
			mottagare.setOrganisation(mottagarOrganisation);
			adressering.setMottagare(mottagare);

			// Skickades tidpunkt
			adressering.setSkickades(inSkickadesTid);

			/********* Fält *******/
			// Smittskydd - Fält 1
			Smittskydd smittskydd = new Smittskydd();
			lakarintyg.setSmittskydd(smittskydd); // Element should always be
													// set!
			if (inSmittskydd) {
				smittskydd.setBeskrivning(rb.getString("FK_SMIL"));
			}

			// Diagnos - Fält 2
			Diagnos diagnos = new Diagnos();
			lakarintyg.setDiagnos(diagnos);
			if (inDiagnoskod != null && inDiagnoskod.length() > 0) {
				diagnos.setKod(inDiagnoskod);
			}
			if (inDiagnosBeskrivning != null && inDiagnosBeskrivning.length() > 0) {
				diagnos.setBeskrivning(inDiagnosBeskrivning);
			}

			// Sjukdomshistoria - Fält 3
			Sjukdomshistoria sjukdomshistoria = new Sjukdomshistoria();
			lakarintyg.setSjukdomshistoria(sjukdomshistoria);
			if (inSjukdomshistoriaBeskrivning != null && inSjukdomshistoriaBeskrivning.length() > 0) {
				sjukdomshistoria.setBeskrivning(inSjukdomshistoriaBeskrivning);
			}

			// Status - Fält 4 - vänster
			Status status = new Status();
			lakarintyg.setStatus(status);
			if (inKroppsFunktion != null) {
				status.setBeskrivning(inKroppsFunktion.getBeskrivning());
			}

			// Ursprung - Fält 4
			Basering basering = new Basering();
			status.setBasering(basering);

			// Ursprung - Fält 4 - höger översta kryssrutan
			if (inUndersokning != null) {
				Ursprung ursprung = new Ursprung();
				basering.getUrsprung().add(ursprung);
				ursprung.setBeskrivning(rb.getString("FK_UNDERSOKNING"));
				ursprung.setDatum(inUndersokning.getVardkontaktstid());
			}

			// Ursprung - Fält 4 - höger näst översta kryssrutan
			if (inTelefonkontakt != null) {
				Ursprung ursprung = new Ursprung();
				basering.getUrsprung().add(ursprung);
				ursprung.setBeskrivning("Telefonkontakt");
				ursprung.setDatum(inTelefonkontakt.getVardkontaktstid());
			}

			// Ursprung - Fält 4 - höger näst nedersta kryssrutan
			if (inJournal != null) {
				Ursprung ursprung = new Ursprung();
				basering.getUrsprung().add(ursprung);
				ursprung.setBeskrivning("Journaluppgifter");
				ursprung.setDatum(inJournal.getDatum());
			}

			// Ursprung - Fält 4 - höger nedersta kryssrutan
			if (inAnnat != null) {
				Ursprung ursprung = new Ursprung();
				basering.getUrsprung().add(ursprung);
				ursprung.setBeskrivning("Annat");
				ursprung.setDatum(inAnnat.getDatum());
			}

			// Status - Fält 5
			Begransning begransning = new Begransning();
			lakarintyg.setBegransning(begransning);
			if (inAktivitetFunktion != null) {
				begransning.setBeskrivning(inAktivitetFunktion.getBeskrivning());
			}

			// Fält 6a
			Rekommendationer rekommendationer = new Rekommendationer();
			lakarintyg.setRekommendationer(rekommendationer);

			// Fält 6a - kryssruta 1
			if (kontaktAF != null) {
				Rekommendation rekommendation = new Rekommendation();
				rekommendation.setBeskrivning(rb.getString("FK_ARBETSFORMEDLING"));
				rekommendationer.getRekommendation().add(rekommendation);
			}

			// Fält 6a - kryssruta 2
			if (kontaktFHV != null) {
				Rekommendation rekommendation = new Rekommendation();
				rekommendation.setBeskrivning(rb.getString("FK_FTGHLSVARD"));
				rekommendationer.getRekommendation().add(rekommendation);
			}

			// Fält 6a - kryssruta 3
			if (ovrigt != null) {
				Rekommendation rekommendation = new Rekommendation();
				rekommendation.setBeskrivning(rb.getString("FK_OVRIGT"));
				rekommendation.setKommentar(ovrigt.getBeskrivning());
				rekommendationer.getRekommendation().add(rekommendation);
			}

			// Fält 6b
			Planering planering = new Planering();
			lakarintyg.setPlanering(planering);

			// Fält 6b - kryssruta 1
			if (planeradAtgardInomSjukvarden != null) {
				Behandling behandling = new Behandling();
				behandling.setBeskrivning(rb.getString("FK_ATGARD"));
				behandling.setKommentar(planeradAtgardInomSjukvarden.getBeskrivning());
				planering.getBehandling().add(behandling);
			}

			// Fält 6b - kryssruta 2
			if (planeradAtgardAnnan != null) {
				Behandling behandling = new Behandling();
				behandling.setBeskrivning("Annan");
				behandling.setKommentar(planeradAtgardAnnan.getBeskrivning());
				planering.getBehandling().add(behandling);
			}

			// Fält 7
			Rehabilitering rehab = new Rehabilitering();
			lakarintyg.setRehabilitering(rehab);
			if (arbRelRehabAktuell != null) {
				rehab.setBeskrivning(rb.getString("FK_ARBLIV_REHAB_AKT"));
			}
			if (arbRelRehabEjAktuell != null) {
				rehab.setBeskrivning(rb.getString("FK_ARBLIV_REHAB_EJ_AKT"));
			}
			if (garEjAttBedommaArbRelRehab != null) {
				rehab.setBeskrivning(rb.getString("FK_BEHOV_EJ_BEDOMMA"));
			}

			// Fält 8a
			Sysselsattning sysselsattning = new Sysselsattning();
			lakarintyg.setSysselsattning(sysselsattning);

			// Fält 8a - kryssruta 1
			if (inArbete != null) {
				sysselsattning.getBeskrivning().add(rb.getString("FK_ARBETAR"));
				if (inAktivitetFunktion.getArbetsformaga().getArbetsuppgift() != null
						&& inAktivitetFunktion.getArbetsformaga().getArbetsuppgift().getTypAvArbetsuppgift() != null) {
					sysselsattning.setArbetsuppgifter(inAktivitetFunktion.getArbetsformaga().getArbetsuppgift()
							.getTypAvArbetsuppgift());
				}
			}

			// Fält 8a - kryssruta 2
			if (inArbetslos != null) {
				sysselsattning.getBeskrivning().add(rb.getString("FK_ARBETSLOS"));
			}

			// Fält 8a - kryssruta 3
			if (inForaldraledig != null) {
				sysselsattning.getBeskrivning().add(rb.getString("FK_FORALDRALEDIG"));
			}

			// Fält 8b
			Arbetsformaga arbetsformaga = new Arbetsformaga();
			lakarintyg.setArbetsformaga(arbetsformaga);

			// Fält 8b - kryssruta 1
			if (nedsatt14del != null) {
				Nedsattningsgrad nedsattningsgrad = new Nedsattningsgrad();
				nedsattningsgrad.setBeskrivning("Nedsatt med 1/4");
				nedsattningsgrad.setFranOchMed(nedsatt14del.getVaraktighetFrom());
				nedsattningsgrad.setLangstTillOchMed(nedsatt14del.getVaraktighetTom());
				lakarintyg.getArbetsformaga().getNedsattningsgrad().add(nedsattningsgrad);
			}

			// Fält 8b - kryssruta 2
			if (nedsatthalften != null) {
				Nedsattningsgrad nedsattningsgrad = new Nedsattningsgrad();
				nedsattningsgrad.setBeskrivning(rb.getString("FK_NEDSATT_HALFTEN"));
				nedsattningsgrad.setFranOchMed(nedsatthalften.getVaraktighetFrom());
				nedsattningsgrad.setLangstTillOchMed(nedsatthalften.getVaraktighetTom());
				lakarintyg.getArbetsformaga().getNedsattningsgrad().add(nedsattningsgrad);
			}

			// Fält 8b - kryssruta 3
			if (nedsatt34delar != null) {
				Nedsattningsgrad nedsattningsgrad = new Nedsattningsgrad();
				nedsattningsgrad.setBeskrivning("Nedsatt med 3/4");
				nedsattningsgrad.setFranOchMed(nedsatt34delar.getVaraktighetFrom());
				nedsattningsgrad.setLangstTillOchMed(nedsatt34delar.getVaraktighetTom());
				lakarintyg.getArbetsformaga().getNedsattningsgrad().add(nedsattningsgrad);
			}

			// Fält 8b - kryssruta 4
			if (heltNedsatt != null) {
				Nedsattningsgrad nedsattningsgrad = new Nedsattningsgrad();
				nedsattningsgrad.setBeskrivning("Helt nedsatt");
				nedsattningsgrad.setFranOchMed(heltNedsatt.getVaraktighetFrom());
				nedsattningsgrad.setLangstTillOchMed(heltNedsatt.getVaraktighetTom());
				lakarintyg.getArbetsformaga().getNedsattningsgrad().add(nedsattningsgrad);
			}

			// Fält 9 - Motivering
			Motivering motivering = new Motivering();
			lakarintyg.setMotivering(motivering);
			if (inMotivering != null && inMotivering.length() > 0) {
				motivering.setBeskrivning(inMotivering);
			}

			// Fält 10 - Prognosangivelse
			Prognos prognos = new Prognos();
			lakarintyg.setPrognos(prognos);
			if (inPrognosAterfaHelt) {
				prognos.setBeskrivning(rb.getString("FK_ATERFA"));
			}
			if (inPrognosAterfaDelvis) {
				prognos.setBeskrivning(rb.getString("FK_DELVIS_ATERFA"));
			}
			if (inPrognosEjAterfa) {
				prognos.setBeskrivning(rb.getString("FK_EJ_ATERFA"));
			}
			if (inPrognosGarEjAttBedomma) {
				prognos.setBeskrivning(rb.getString("FK_EJ_BEDOMA_ATERFA"));
			}

			// Fält 11 - kryssruta 1 och 2 Endast 1 av 2 val möjligt!
			Resor resor = new Resor();
			lakarintyg.setResor(resor);
			if (inResorJa) {
				resor.setBeskrivning(rb.getString("FK_ANNAT_FARDSATT"));
			}
			if (inResorNej) {
				resor.setBeskrivning(rb.getString("FK_ANNAT_FARDSATT_EJ"));
			}

			// Fält 12 - kryssruta 1 och 2
			Kontakt kontakt = new Kontakt();
			lakarintyg.setKontakt(kontakt);
			if (inKontaktFK) {
				kontakt.setBeskrivning(rb.getString("FK_KONTAKT_FK"));
			}

			// Fält 13 - Upplysningar
			Upplysningar upplysningar = new Upplysningar();
			lakarintyg.setUpplysningar(upplysningar);
			if (inKommentar != null && inKommentar.length() > 0) {
				upplysningar.setBeskrivning(inKommentar);
			}

			// Fält 14 - 17
			Lakare lakare = new Lakare();
			lakarintyg.setLakare(lakare);

			// Fält 14 - Signeringstidpunkt
			lakare.setSignerades(inSignerades);

			// Fält 17
			if (inEnhetsArbetsplatskod != null && inEnhetsArbetsplatskod.length() > 0) {
				lakare.setArbetsplatskod(inEnhetsArbetsplatskod);
			}
			if (inLakarForskrivarekod != null && inLakarForskrivarekod.length() > 0) {
				lakare.setForskrivarkod(inLakarForskrivarekod);
			}

			fkSklTELA.setLakarintyg(lakarintyg);

			AttributedURIType logicalAddressHeader = new AttributedURIType();
			logicalAddressHeader.setValue(FK_ID);

			Object[] payloadOut = new Object[] { logicalAddressHeader, outRequest };

			if (logger.isDebugEnabled()) {
				logger.debug("transformed payload to: " + payloadOut);
			}

			logger.info("Exiting vard2fk register medical certificate transform");

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

}
