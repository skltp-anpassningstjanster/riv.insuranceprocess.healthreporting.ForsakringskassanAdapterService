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
package se.tp.fk.vardgivare.sjukvard.taemotsvar.consumer;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3.wsaddressing10.AttributedURIType;

import se.fk.vardgivare.sjukvard.taemotsvar.v1.rivtabp20.TaEmotSvarResponderInterface;
import se.fk.vardgivare.sjukvard.taemotsvar.v1.rivtabp20.TaEmotSvarResponderService;
import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarResponseType;
import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarType;
import se.fk.vardgivare.sjukvard.v1.Adressering;
import se.fk.vardgivare.sjukvard.v1.Enhet;
import se.fk.vardgivare.sjukvard.v1.InternIdentitetsbeteckning;
import se.fk.vardgivare.sjukvard.v1.Lakarintygsreferens;
import se.fk.vardgivare.sjukvard.v1.Organisation;
import se.fk.vardgivare.sjukvard.v1.TaEmotSvar;
import se.fk.vardgivare.sjukvard.v1.Adressering.Mottagare;

public final class TaEmotSvarConsumer {

	// Use this one to connect via Virtualiseringsplattformen
	private static final String LOGISK_ADDRESS = "/TaEmotSvar/1/rivtabp20";

	// Use this one to connect directly (just for test)

	public static void main(String[] args) {
		String host = "localhost:19000/fk";
		if (args.length > 0) {
			host = args[0];
		}

		// Setup ssl info for the initial ?wsdl lookup...
		System.setProperty("javax.net.ssl.keyStore","../../certs/consumer.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
		System.setProperty("javax.net.ssl.trustStore","../../certs/truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "password");

		String adress = "https://" + host + LOGISK_ADDRESS;
		System.out.println("Consumer connecting to " + adress);
		String p = callTaEmotSvar("RIV TA BP2.0 Ref App OK", adress,"Test HSA-ID");
		System.out.println("Returned: " + p);
	}

	public static String callTaEmotSvar(String id, String serviceAddress,
			String logicalAddresss) {

		TaEmotSvarResponderInterface service = new TaEmotSvarResponderService(
				createEndpointUrlFromServiceAddress(serviceAddress)).getTaEmotSvarResponderPort();

		AttributedURIType logicalAddressHeader = new AttributedURIType();
		logicalAddressHeader.setValue(logicalAddresss);

		TaEmotSvarType request = new TaEmotSvarType();
		
		// Simple Svar
		TaEmotSvar fkSvar = new TaEmotSvar();
		request.setFKSKLTaEmotSvarAnrop(fkSvar);
		fkSvar.setAdressering(getAdressering());
		fkSvar.setLakarintyg(getSimpleLakarintyg());
		
		try {
			TaEmotSvarResponseType result = service.taEmotSvar(logicalAddressHeader, request);

			if (result != null) {
				return ("Result OK");
			} else {
				return ("Result Error!");				
			}

		} catch (Exception ex) {
			System.out.println("Exception=" + ex.getMessage());
			return null;
		}
	}

	private static Adressering getAdressering() {
		Adressering adressering = new Adressering();
		Mottagare mottagare = new Mottagare();
		Organisation mottagarOrg = new Organisation();
		Enhet mottagarEnhet = new Enhet();
		InternIdentitetsbeteckning mottEnhetId = new InternIdentitetsbeteckning();
		mottEnhetId.setValue("kalle");
		mottagarEnhet.setId(mottEnhetId);
		mottagarOrg.setEnhet(mottagarEnhet);
		mottagare.setOrganisation(mottagarOrg);
		adressering.setMottagare(mottagare);
		return adressering;
	}

	private static Lakarintygsreferens getSimpleLakarintyg() {
		Lakarintygsreferens lakarintygRef = new Lakarintygsreferens();
		return lakarintygRef;
	}
	
	public static URL createEndpointUrlFromServiceAddress(String serviceAddress) {
		try {
			return new URL(serviceAddress + "?wsdl");
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: "
					+ e.getMessage());
		}
	}
}
