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
package se.skl.skltpservices.adapter.fk.regmedcert;

import java.util.List;

import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.AktivitetType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Aktivitetskod;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.ArbetsformagaNedsattningType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.FunktionstillstandType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.ReferensType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Referenstyp;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.SysselsattningType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.TypAvFunktionstillstand;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.TypAvSysselsattning;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.VardkontaktType;
import se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Vardkontakttyp;

public final class RegisterMedicalCertificateUtil {

	public static AktivitetType findAktivitetWithCode(List<AktivitetType> aktiviteter, Aktivitetskod aktivitetskod) {
		AktivitetType foundAktivitet = null;
		if (aktiviteter != null) {
			for (int i = 0; i < aktiviteter.size(); i++) {
				AktivitetType listAktivitet = (AktivitetType) aktiviteter.get(i);
				if (listAktivitet.getAktivitetskod() != null
						&& listAktivitet.getAktivitetskod().compareTo(aktivitetskod) == 0) {
					foundAktivitet = listAktivitet;
					break;
				}
			}
		}
		return foundAktivitet;
	}

	public static FunktionstillstandType findFunktionsTillstandType(List<FunktionstillstandType> funktionstillstand,
			TypAvFunktionstillstand funktionstillstandsTyp) {
		FunktionstillstandType foundFunktionstillstand = null;
		if (funktionstillstand != null) {
			for (int i = 0; i < funktionstillstand.size(); i++) {
				FunktionstillstandType listFunktionstillstand = (FunktionstillstandType) funktionstillstand.get(i);
				if (listFunktionstillstand.getTypAvFunktionstillstand() != null
						&& listFunktionstillstand.getTypAvFunktionstillstand().compareTo(funktionstillstandsTyp) == 0) {
					foundFunktionstillstand = listFunktionstillstand;
					break;
				}
			}
		}
		return foundFunktionstillstand;
	}

	public static VardkontaktType findVardkontaktTyp(List<VardkontaktType> vardkontakter, Vardkontakttyp vardkontaktTyp) {
		VardkontaktType foundVardkontaktType = null;
		if (vardkontakter != null) {
			for (int i = 0; i < vardkontakter.size(); i++) {
				VardkontaktType listVardkontakter = (VardkontaktType) vardkontakter.get(i);
				if (listVardkontakter.getVardkontakttyp() != null
						&& listVardkontakter.getVardkontakttyp().compareTo(vardkontaktTyp) == 0) {
					foundVardkontaktType = listVardkontakter;
					break;
				}
			}
		}
		return foundVardkontaktType;
	}

	public static ReferensType findReferensTyp(List<ReferensType> referenser, Referenstyp referensTyp) {
		ReferensType foundReferensType = null;
		if (referenser != null) {
			for (int i = 0; i < referenser.size(); i++) {
				ReferensType listReferenser = (ReferensType) referenser.get(i);
				if (listReferenser.getReferenstyp() != null
						&& listReferenser.getReferenstyp().compareTo(referensTyp) == 0) {
					foundReferensType = listReferenser;
					break;
				}
			}
		}
		return foundReferensType;
	}

	public static SysselsattningType findTypAvSysselsattning(List<SysselsattningType> sysselsattning,
			TypAvSysselsattning sysselsattningsTyp) {
		SysselsattningType foundSysselsattningType = null;
		if (sysselsattning != null) {
			for (int i = 0; i < sysselsattning.size(); i++) {
				SysselsattningType listSysselsattning = (SysselsattningType) sysselsattning.get(i);
				if (listSysselsattning.getTypAvSysselsattning() != null
						&& listSysselsattning.getTypAvSysselsattning().compareTo(sysselsattningsTyp) == 0) {
					foundSysselsattningType = listSysselsattning;
					break;
				}
			}
		}
		return foundSysselsattningType;
	}

	public static ArbetsformagaNedsattningType findArbetsformaga(List<ArbetsformagaNedsattningType> arbetsformaga,
			se.inera.ifv.insuranceprocess.healthreporting.mu7263.v3.Nedsattningsgrad arbetsformagaNedsattningTyp) {
		ArbetsformagaNedsattningType foundArbetsformagaType = null;
		if (arbetsformaga != null) {
			for (int i = 0; i < arbetsformaga.size(); i++) {
				ArbetsformagaNedsattningType listArbetsformaga = (ArbetsformagaNedsattningType) arbetsformaga.get(i);
				if (listArbetsformaga.getNedsattningsgrad() != null
						&& listArbetsformaga.getNedsattningsgrad().compareTo(arbetsformagaNedsattningTyp) == 0) {
					foundArbetsformagaType = listArbetsformaga;
					break;
				}
			}
		}
		return foundArbetsformagaType;
	}
}
