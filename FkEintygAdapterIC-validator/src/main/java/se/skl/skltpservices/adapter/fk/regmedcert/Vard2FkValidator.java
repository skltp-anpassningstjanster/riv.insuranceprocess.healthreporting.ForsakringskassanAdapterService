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
import static se.skl.skltpservices.adapter.fk.util.ValidatorUtil.getValidationErrors;
import static se.skl.skltpservices.adapter.fk.util.ValidatorUtil.validateHoSPersonal;
import static se.skl.skltpservices.adapter.fk.util.ValidatorUtil.validatePatient;
import iso.v21090.dt.v1.II;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.AktivitetType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Aktivitetskod;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.ArbetsformagaNedsattningType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.ArbetsuppgiftType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.FunktionstillstandType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.LakarutlatandeType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.MedicinsktTillstandType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Prognosangivelse;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.ReferensType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Referenstyp;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.SysselsattningType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.TypAvFunktionstillstand;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.TypAvSysselsattning;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.VardkontaktType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Vardkontakttyp;
import se.inera.ifv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v3.RegisterMedicalCertificateType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.EnhetType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.PatientType;

public class Vard2FkValidator {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void validateRequest(RegisterMedicalCertificateType inRequest) throws Exception {
		// List of validation errors
		ArrayList<String> validationErrors = new ArrayList<String>();

		// Validate incoming request
		try {
			// Check that we got any data at all
			if (inRequest == null) {
				validationErrors.add("No RegisterMedicalCertificate found in incoming data!");
				throw new Exception();
			}

			// Check that we got a lakarutlatande element
			if (inRequest.getLakarutlatande() == null) {
				validationErrors.add("No Lakarutlatande element found in incoming request data!");
				throw new Exception();
			}

			LakarutlatandeType inLakarutlatande = inRequest.getLakarutlatande();

			/**
			 * Check all meta-data, that is data not shown in the form
			 */

			// Check that we got an id - mandatory
			if (inLakarutlatande.getLakarutlatandeId() == null || inLakarutlatande.getLakarutlatandeId().length() < 1) {
				validationErrors.add("No Lakarutlatande Id found!");
			}

			// Check skickat datum - mandatory
			if (inLakarutlatande.getSkickatDatum() == null) {
				validationErrors.add("No or wrong skickatDatum found!");
			}

			/**
			 * Check patient information
			 */
			PatientType inPatient = inLakarutlatande.getPatient();
			validatePatient(validationErrors, inPatient);

			/**
			 * Check hälso och sjukvårds personal information. Vårdgivare,
			 * vårdenhet och läkare.
			 */
			// Check that we got a skapadAvHosPersonal element
			if (inLakarutlatande.getSkapadAvHosPersonal() == null) {
				validationErrors.add("No SkapadAvHosPersonal element found!");
				throw new Exception();
			}
			HosPersonalType inHoSP = inLakarutlatande.getSkapadAvHosPersonal();
	        validateHoSPersonal(validationErrors, inHoSP);

            // Additional validation for enhet
			EnhetType inEnhet = inHoSP.getEnhet();
			// Check enhetsadress - mandatory
			if (inEnhet.getPostadress() == null || inEnhet.getPostadress().length() < 1) {
				validationErrors.add("No postadress found for enhet!");
			}
			if (inEnhet.getPostnummer() == null || inEnhet.getPostnummer().length() < 1) {
				validationErrors.add("No postnummer found for enhet!");
			}
			if (inEnhet.getPostort() == null || inEnhet.getPostort().length() < 1) {
				validationErrors.add("No postort found for enhet!");
			}
			if (inEnhet.getTelefonnummer() == null || inEnhet.getTelefonnummer().length() < 1) {
				validationErrors.add("No telefonnummer found for enhet!");
			}

			/**
			 * Check form data
			 */
			// Fält 1 - no rule
			boolean inSmittskydd = findAktivitetWithCode(inRequest.getLakarutlatande().getAktivitet(),
					Aktivitetskod.AVSTANGNING_ENLIGT_SM_L_PGA_SMITTA) != null ? true : false;

			// Must be set as this element contains a lot of mandatory
			// information
			FunktionstillstandType inAktivitetFunktion = findFunktionsTillstandType(
					inLakarutlatande.getFunktionstillstand(), TypAvFunktionstillstand.AKTIVITET);
			if (inAktivitetFunktion == null) {
				validationErrors.add("No funktionstillstand - aktivitet element found!");
				throw new Exception();
			}

			// Declared outside as it may be used further down.
			ReferensType inAnnat = null;

			// Many fields are optional if smittskydd is checked, if not set
			// validate these below
			if (!inSmittskydd) {
				// Fält 2 - Check that we got a medicinsktTillstand element
				if (inLakarutlatande.getMedicinsktTillstand() == null) {
					validationErrors.add("No medicinsktTillstand element found!");
					throw new Exception();
				}
				// Fält 2 - Medicinskt tillstånd kod - mandatory
				MedicinsktTillstandType medTillstand = inLakarutlatande.getMedicinsktTillstand();
				if (medTillstand.getTillstandskod() == null || medTillstand.getTillstandskod().getCode() == null
						|| medTillstand.getTillstandskod().getCode().length() < 1) {
					validationErrors.add("No tillstandskod in medicinsktTillstand found!");
				}
				// Fält 2 - Medicinskt tillstånd kodsystemnamn - mandatory
				if (medTillstand.getTillstandskod() == null
						|| medTillstand.getTillstandskod().getCodeSystemName() == null
						|| !medTillstand.getTillstandskod().getCodeSystemName().equalsIgnoreCase("ICD-10")) {
					validationErrors
							.add("Wrong code system name for medicinskt tillstand - tillstandskod (diagnoskod)! Should be ICD-10");
				}
				// Fält 2 - Medicinskt tillstånd beskrivning - optional

				// Fält 3 - Not mandatory

				// Fält 4 - vänster Check that we got a funktionstillstand -
				// kroppsfunktion element
				FunktionstillstandType inKroppsFunktion = findFunktionsTillstandType(
						inLakarutlatande.getFunktionstillstand(), TypAvFunktionstillstand.KROPPSFUNKTION);
				if (inKroppsFunktion == null) {
					validationErrors.add("No funktionstillstand - kroppsfunktion element found!");
					throw new Exception();
				}
				// Fält 4 - vänster Funktionstillstand - kroppsfunktion
				// beskrivning - mandatory
				if (inKroppsFunktion.getBeskrivning() == null || inKroppsFunktion.getBeskrivning().length() < 1) {
					validationErrors.add("No beskrivning in funktionstillstand - kroppsfunktion found!");
				}

				// Fält 4 - höger översta kryssrutan
				VardkontaktType inUndersokning = findVardkontaktTyp(inLakarutlatande.getVardkontakt(),
						Vardkontakttyp.MIN_UNDERSOKNING_AV_PATIENTEN);

				// Fält 4 - höger näst översta kryssrutan
				VardkontaktType telefonkontakt = findVardkontaktTyp(inLakarutlatande.getVardkontakt(),
						Vardkontakttyp.MIN_TELEFONKONTAKT_MED_PATIENTEN);

				// Fält 4 - höger näst nedersta kryssrutan
				ReferensType journal = findReferensTyp(inLakarutlatande.getReferens(), Referenstyp.JOURNALUPPGIFTER);

				// Fält 4 - höger nedersta kryssrutan
				inAnnat = findReferensTyp(inLakarutlatande.getReferens(), Referenstyp.ANNAT);

				// Fält 4 - höger Check that we at least got one field set
				if (inUndersokning == null && telefonkontakt == null && journal == null && inAnnat == null) {
					validationErrors.add("No vardkontakt or referens element found ! At least one must be set!");
					throw new Exception();
				}
				// Fält 4 - höger - 1:a kryssrutan Check that we got a date if
				// choice is set
				if (inUndersokning != null
						&& (inUndersokning.getVardkontaktstid() == null)) {
					validationErrors.add("No or wrong date for vardkontakt - min undersokning av patienten found!");
				}
				// Fält 4 - höger - 2:a kryssrutan Check that we got a date if
				// choice is set
				if (telefonkontakt != null
						&& (telefonkontakt.getVardkontaktstid() == null)) {
					validationErrors.add("No or wrong date for vardkontakt - telefonkontakt found!");
				}
				// Fält 4 - höger - 3:e kryssrutan Check that we got a date if
				// choice is set
				if (journal != null && (journal.getDatum() == null)) {
					validationErrors.add("No or wrong date for referens - journal found!");
				}
				// Fält 4 - höger - 4:e kryssrutan Check that we got a date if
				// choice is set
				if (inAnnat != null && (inAnnat.getDatum() == null)) {
					validationErrors.add("No or wrong date for referens - annat found!");
				}

				// Fält 5 - not mandatory

				// Fält 6 - not mandatory

				// Fält 7 - not mandatory

				// Fält 8a - Check that we got a arbetsformaga element
				if (inAktivitetFunktion.getArbetsformaga() == null) {
					validationErrors.add("No arbetsformaga element found for field 8a!");
					throw new Exception();
				}

				// Fält 8a
				SysselsattningType inArbete = findTypAvSysselsattning(inAktivitetFunktion.getArbetsformaga()
						.getSysselsattning(), TypAvSysselsattning.NUVARANDE_ARBETE);
				SysselsattningType inArbetslos = findTypAvSysselsattning(inAktivitetFunktion.getArbetsformaga()
						.getSysselsattning(), TypAvSysselsattning.ARBETSLOSHET);
				SysselsattningType inForaldraledig = findTypAvSysselsattning(inAktivitetFunktion.getArbetsformaga()
						.getSysselsattning(), TypAvSysselsattning.FORALDRALEDIGHET);
				// Fält 8a - Check that we at least got one choice
				if (inArbete == null && inArbetslos == null && inForaldraledig == null) {
					validationErrors
							.add("No sysselsattning element found for field 8a! Nuvarande arbete, arbestloshet or foraldraledig should be set.");
					throw new Exception();
				}
				ArbetsuppgiftType inArbetsBeskrivning = inAktivitetFunktion.getArbetsformaga().getArbetsuppgift();
				// Fält 8a - Check that we got a arbetsuppgift element if arbete
				// is set
				if (inArbete != null && inArbetsBeskrivning == null) {
					validationErrors.add("No arbetsuppgift element found when arbete set in field 8a!.");
					throw new Exception();
				}
				// Fält 8a - 1:a kryssrutan - beskrivning
				if (inArbete != null
						&& (inArbetsBeskrivning.getTypAvArbetsuppgift() == null || inArbetsBeskrivning
								.getTypAvArbetsuppgift().length() < 1)) {
					validationErrors.add("No typAvArbetsuppgift found when arbete set in field 8a!.");
					throw new Exception();
				}
			}

			// Fält 8b - kryssruta 1
			ArbetsformagaNedsattningType nedsatt14del = findArbetsformaga(inAktivitetFunktion.getArbetsformaga()
					.getArbetsformagaNedsattning(),
					se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad.NEDSATT_MED_1_4);

			// Fält 8b - kryssruta 2
			ArbetsformagaNedsattningType nedsatthalften = findArbetsformaga(inAktivitetFunktion.getArbetsformaga()
					.getArbetsformagaNedsattning(),
					se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad.NEDSATT_MED_1_2);

			// Fält 8b - kryssruta 3
			ArbetsformagaNedsattningType nedsatt34delar = findArbetsformaga(inAktivitetFunktion.getArbetsformaga()
					.getArbetsformagaNedsattning(),
					se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad.NEDSATT_MED_3_4);

			// Fält 8b - kryssruta 4
			ArbetsformagaNedsattningType heltNedsatt = findArbetsformaga(inAktivitetFunktion.getArbetsformaga()
					.getArbetsformagaNedsattning(),
					se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad.HELT_NEDSATT);

			// Check that we at least got one choice
			if (nedsatt14del == null && nedsatthalften == null && nedsatt34delar == null && heltNedsatt == null) {
				validationErrors.add("No arbetsformaganedsattning element found 8b!.");
				throw new Exception();
			}
			// Fält 8b - kryssruta 1 - varaktighet From
			if (nedsatt14del != null
					&& (nedsatt14del.getVaraktighetFrom() == null)) {
				validationErrors.add("No or wrong date for nedsatt 1/4 from date found!");
			}
			// Fält 8b - kryssruta 1 - varaktighet Tom
			if (nedsatt14del != null
					&& (nedsatt14del.getVaraktighetTom() == null)) {
				validationErrors.add("No or wrong date for nedsatt 1/4 tom date found!");
			}
			// Fält 8b - kryssruta 2 - varaktighet From
			if (nedsatthalften != null
					&& (nedsatthalften.getVaraktighetFrom() == null)) {
				validationErrors.add("No or wrong date for nedsatt 1/2 from date found!");
			}
			// Fält 8b - kryssruta 2 - varaktighet Tom
			if (nedsatthalften != null
					&& (nedsatthalften.getVaraktighetTom() == null)) {
				validationErrors.add("No or wrong date for nedsatt 1/2 tom date found!");
			}
			// Fält 8b - kryssruta 3 - varaktighet From
			if (nedsatt34delar != null
					&& (nedsatt34delar.getVaraktighetFrom() == null)) {
				validationErrors.add("No or wrong date for nedsatt 3/4 from date found!");
			}
			// Fält 8b - kryssruta 3 - varaktighet Tom
			if (nedsatt34delar != null
					&& (nedsatt34delar.getVaraktighetTom() == null)) {
				validationErrors.add("No or wrong date for nedsatt 3/4 tom date found!");
			}
			// Fält 8b - kryssruta 4 - varaktighet From
			if (heltNedsatt != null
					&& (heltNedsatt.getVaraktighetFrom() == null)) {
				validationErrors.add("No or wrong date for helt nedsatt from date found!");
			}
			// Fält 8b - kryssruta 4 - varaktighet Tom
			if (heltNedsatt != null
					&& (heltNedsatt.getVaraktighetTom() == null)) {
				validationErrors.add("No or wrong date for helt nedsatt tom date found!");
			}

			// Fält 9 - Motivering - optional

			// Fält 10 - Prognosangivelse - optional
			boolean inArbetsformagaAterstallasHelt = false;
			boolean inArbetsformagaAterstallasDelvis = false;
			boolean inArbetsformagaEjAterstallas = false;
			boolean inArbetsformagaGarEjAttBedomma = false;

			if (inAktivitetFunktion.getArbetsformaga().getPrognosangivelse() != null) {
				inArbetsformagaAterstallasHelt = inAktivitetFunktion.getArbetsformaga().getPrognosangivelse()
						.compareTo(Prognosangivelse.ATERSTALLAS_HELT) == 0;
				inArbetsformagaAterstallasDelvis = inAktivitetFunktion.getArbetsformaga().getPrognosangivelse()
						.compareTo(Prognosangivelse.ATERSTALLAS_DELVIS) == 0;
				inArbetsformagaEjAterstallas = inAktivitetFunktion.getArbetsformaga().getPrognosangivelse()
						.compareTo(Prognosangivelse.INTE_ATERSTALLAS) == 0;
				inArbetsformagaGarEjAttBedomma = inAktivitetFunktion.getArbetsformaga().getPrognosangivelse()
						.compareTo(Prognosangivelse.DET_GAR_INTE_ATT_BEDOMMA) == 0;
			}

			// If we got more then one prognoselement these will not be read as
			// only the first is set!
			int inPrognosCount = 0;
			if (inArbetsformagaAterstallasHelt) {
				inPrognosCount++;
			}
			if (inArbetsformagaAterstallasDelvis) {
				inPrognosCount++;
			}
			if (inArbetsformagaEjAterstallas) {
				inPrognosCount++;
			}
			if (inArbetsformagaGarEjAttBedomma) {
				inPrognosCount++;
			}

			// Fält 10 - Prognosangivelse - Check that we only got one choice
			if (inPrognosCount > 2) {
				validationErrors.add("Only one prognosangivelse should be set for field 10.");
			}

			// Fält 11 - optional
			AktivitetType inForandratRessatt = findAktivitetWithCode(inLakarutlatande.getAktivitet(),
					Aktivitetskod.FORANDRAT_RESSATT_TILL_ARBETSPLATSEN_AR_AKTUELLT);
			AktivitetType inEjForandratRessatt = findAktivitetWithCode(inLakarutlatande.getAktivitet(),
					Aktivitetskod.FORANDRAT_RESSATT_TILL_ARBETSPLATSEN_AR_EJ_AKTUELLT);

			// Fält 11 - If set only one should be set
			if (inForandratRessatt != null && inEjForandratRessatt != null) {
				validationErrors.add("Only one forandrat ressatt could be set for field 11.");
			}

			// Fält 12 - kryssruta 1 - optional

			// Fält 13 - Upplysningar - optional
			// If field 4 annat satt or field 10 går ej att bedömma is set then
			// field 13 should contain data.
			String kommentar = inRequest.getLakarutlatande().getKommentar();
			if ((inAnnat != null || inArbetsformagaGarEjAttBedomma) && (kommentar == null || kommentar.length() < 1)) {
				validationErrors.add("Upplysningar should contain data as field 4 or fields 10 is checked.");
			}

			// Fält 14 - Signeringstidpunkt
			if (inLakarutlatande.getSigneringsdatum() == null) {
				validationErrors.add("Signeringsdatum must be set (14)");
			}

			// Fält 17 - arbetsplatskod - Check that we got an element
			if (inEnhet.getArbetsplatskod() == null) {
				validationErrors.add("No Arbetsplatskod element found!");
				throw new Exception();
			}
			II inArbetsplatskod = inEnhet.getArbetsplatskod();
			// Fält 17 arbetsplatskod id
			if (inArbetsplatskod.getExtension() == null || inArbetsplatskod.getExtension().length() < 1) {
				validationErrors.add("Arbetsplatskod for enhet not found!");
			}
			// Fält 17 arbetsplatskod o.i.d.
			if (inArbetsplatskod.getRoot() == null || !inArbetsplatskod.getRoot().equalsIgnoreCase("1.2.752.29.4.71")) {
				validationErrors.add("Wrong o.i.d. for arbetsplatskod! Should be 1.2.752.29.4.71");
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

}
