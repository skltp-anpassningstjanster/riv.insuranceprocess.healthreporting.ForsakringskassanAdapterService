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
package se.tp.fk.vardgivare.sjukvard.taemotfraga.consumer;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.w3.wsaddressing10.AttributedURIType;

import se.fk.vardgivare.sjukvard.taemotfraga.v1.rivtabp20.TaEmotFragaResponderInterface;
import se.fk.vardgivare.sjukvard.taemotfraga.v1.rivtabp20.TaEmotFragaResponderService;
import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaResponseType;
import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaType;
import se.fk.vardgivare.sjukvard.v1.Adressering;
import se.fk.vardgivare.sjukvard.v1.Amne;
import se.fk.vardgivare.sjukvard.v1.Enhet;
import se.fk.vardgivare.sjukvard.v1.InternIdentitetsbeteckning;
import se.fk.vardgivare.sjukvard.v1.Meddelande;
import se.fk.vardgivare.sjukvard.v1.Organisation;
import se.fk.vardgivare.sjukvard.v1.TaEmotFraga;
import se.fk.vardgivare.sjukvard.v1.Adressering.Mottagare;

public final class TaEmotFragaConsumer {

	// Use this one to connect via Virtualiseringsplattformen
	private static final String LOGISK_ADDRESS = "/TaEmotFraga/1/rivtabp20";

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

		String p = null;
		try {
			p = callTaEmotFraga("RIV TA BP2.0 Ref App OK", adress,"Test HSA-ID");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Returned: " + p);
	}

	public static String callTaEmotFraga(String id, String serviceAddress,
			String logicalAddresss) throws Exception {

		TaEmotFragaResponderInterface service = new TaEmotFragaResponderService(
				createEndpointUrlFromServiceAddress(serviceAddress)).getTaEmotFragaResponderPort();

		AttributedURIType logicalAddressHeader = new AttributedURIType();
		logicalAddressHeader.setValue(logicalAddresss);

		TaEmotFragaType request = new TaEmotFragaType();
		
		// Simple Fraga
		TaEmotFraga fkFraga = new TaEmotFraga();
		request.setFKSKLTaEmotFragaAnrop(fkFraga);
		fkFraga.setAdressering(getAdressering());
		Amne amne = new Amne();
		amne.setBeskrivning("Möte");
		fkFraga.setAmne(amne);
		Meddelande fraga = new Meddelande();
		fraga.setText("Bra med möte!");
		fraga.setSignerades(getDate("20100203"));
		fkFraga.setFraga(fraga);
		
		
		try {
			TaEmotFragaResponseType result = service.taEmotFraga(logicalAddressHeader, request);

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
	
	private static XMLGregorianCalendar getDate(String stringDate) throws Exception{

		try {
			GregorianCalendar fromDate = new GregorianCalendar();
			DateFormat dfm = new SimpleDateFormat("yyyyMMdd");
			Date date = dfm.parse(stringDate);
			fromDate.setTime(date);
			return (DatatypeFactory.newInstance().newXMLGregorianCalendar(fromDate));
		} catch (DatatypeConfigurationException e) {
			throw new Exception(e.getMessage());
		} catch (ParseException pe) {
			throw new Exception(pe.getMessage());
		}
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
