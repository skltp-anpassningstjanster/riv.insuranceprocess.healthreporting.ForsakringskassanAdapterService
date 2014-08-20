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
package se.skl.skltpservices.adapter.fk.vardgivare.sjukvard.taemotfraga;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.w3.wsaddressing10.AttributedURIType;

import se.fk.vardgivare.sjukvard.taemotfraga.v1.rivtabp20.TaEmotFragaResponderInterface;
import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaResponseType;
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
import se.fk.vardgivare.sjukvard.v1.NationellIdentitetsbeteckning;
import se.fk.vardgivare.sjukvard.v1.Organisation;
import se.fk.vardgivare.sjukvard.v1.Patient;
import se.fk.vardgivare.sjukvard.v1.Person;
import se.fk.vardgivare.sjukvard.v1.Postadress;
import se.fk.vardgivare.sjukvard.v1.Postnummer;
import se.fk.vardgivare.sjukvard.v1.Postort;
import se.fk.vardgivare.sjukvard.v1.ReferensAdressering;
import se.fk.vardgivare.sjukvard.v1.TaEmotFraga;
import se.fk.vardgivare.sjukvard.v1.Telefon;
import se.skl.skltpservices.adapter.common.processor.FkAdapterUtil;
import se.skl.skltpservices.adapter.fk.revokemedcert.RevokeTransformTestConsumer;

public class TaEmotFragaTestConsumer {

	TaEmotFragaResponderInterface _service;

	public TaEmotFragaTestConsumer(String endpointAdress) {
		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setServiceClass(TaEmotFragaResponderInterface.class);
		proxyFactory.setAddress(endpointAdress);
		
		// Used for HTTPS
		SpringBusFactory bf = new SpringBusFactory();
		URL cxfConfig = RevokeTransformTestConsumer.class.getClassLoader().getResource("cxf-test-consumer-config.xml");
		if (cxfConfig != null) {
			proxyFactory.setBus(bf.createBus(cxfConfig));
		}
		
		_service = (TaEmotFragaResponderInterface) proxyFactory.create();
	}
	
	public TaEmotFragaResponseType taEmotFraga() throws DatatypeConfigurationException {
		return taEmotFraga(null);
	}

	public TaEmotFragaResponseType taEmotFraga(String senderId) throws DatatypeConfigurationException {

		TaEmotFragaType request = new TaEmotFragaType();
		TaEmotFraga taEmotFraga = new TaEmotFraga();
		taEmotFraga.setAdressering(createAdressing());
		taEmotFraga.setAmne(createAmne());
		taEmotFraga.setFraga(createFraga());
		taEmotFraga.setLakarintyg(createLakarIntyg());
		taEmotFraga.setPatient(createPatient());
		taEmotFraga.setFraga(createSvar());
		
		request.setFKSKLTaEmotFragaAnrop(taEmotFraga);

		AttributedURIType adressing = new AttributedURIType();
		adressing.setValue("LOGICALADRESS");
		
		setSenderId(senderId);

		return _service.taEmotFraga(adressing, request);
	}

	private Meddelande createSvar() {
		Meddelande meddelande = new Meddelande();
		meddelande.setText("A message");
		return meddelande;
	}

	private Patient createPatient() {
		Patient patient = new Patient();
		patient.setEfternamn("Olsson");
		patient.setFornamn("Olle");
		patient.setIdentifierare("480404-2812");
		patient.setNamn("Olle Olsson");
		return patient;
	}

	private Lakarintygsreferens createLakarIntyg() {
		Lakarintygsreferens lakarintygsreferens = new Lakarintygsreferens();
		lakarintygsreferens.setReferens("Referens");
		return lakarintygsreferens;
	}

	private Meddelande createFraga() {
		Meddelande meddelande = new Meddelande();
		meddelande.setText("A question?");
		return meddelande;
	}

	private Amne createAmne() {
		Amne amne = new Amne();
		amne.setBeskrivning("A description");
		amne.setFritext("A text of something");
		return amne;
	}

	private Adressering createAdressing() throws DatatypeConfigurationException {
		Adressering adressering = new Adressering();
		adressering.setAvsandare(createAvsandare());
		adressering.setMottagare(createMottagare());
		adressering.setSkickades(createTimestamp());
		return adressering;
	}

	private XMLGregorianCalendar createTimestamp() throws DatatypeConfigurationException {
		XMLGregorianCalendar timestamp = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		return timestamp;
	}

	private Mottagare createMottagare() {
		Mottagare mottagare = new Mottagare();
		mottagare.setOrganisation(createOrganisation());
		mottagare.setReferens(createRefAdressing());
		return mottagare;
	}

	private ReferensAdressering createRefAdressing() {
		ReferensAdressering adressering = new ReferensAdressering();
		adressering.setValue("A reference");
		return adressering;
	}

	private Organisation createOrganisation() {
		Organisation organisation = new Organisation();
		organisation.setEnhet(createEnhet());
		organisation.setId(createId());
		organisation.setKontaktuppgifter(createKontaktUppgifter());
		organisation.setNamn(createNamn());
		organisation.setOrganisationsnummer(createOrganisationsnummer());
		return organisation;
	}

	private NationellIdentitetsbeteckning createOrganisationsnummer() {
		NationellIdentitetsbeteckning identitetsbeteckning = new NationellIdentitetsbeteckning();
		identitetsbeteckning.setValue("National id");
		return identitetsbeteckning;
	}

	private Namn createNamn() {
		Namn namn = new Namn();
		namn.setValue("A name");
		return namn;
	}

	private Kontaktuppgifter createKontaktUppgifter() {
		Kontaktuppgifter kontaktuppgifter = new Kontaktuppgifter();
		kontaktuppgifter.setAdress(createAdress());
		kontaktuppgifter.setEpost(createEpotAdress());
		kontaktuppgifter.setTelefon(createTelefon());
		return kontaktuppgifter;
	}

	private Telefon createTelefon() {
		Telefon telefon = new Telefon();
		telefon.setValue("1234567890");
		return telefon;
	}

	private Epostadress createEpotAdress() {
		Epostadress epostadress = new Epostadress();
		epostadress.setValue("a.person@someadress.xyz");
		return epostadress;
	}

	private Adress createAdress() {
		Adress adress = new Adress();
		adress.setLand(createLand());
		adress.setPostadress(createPostAdress());
		adress.setPostnummer(createPostNummer());
		adress.setPostort(createPostOrt());
		return adress;
	}

	private Postort createPostOrt() {
		Postort postort = new Postort();
		postort.setValue("postort");
		return postort;
	}

	private Postnummer createPostNummer() {
		Postnummer postnummer = new Postnummer();
		postnummer.setValue("postummer");
		return postnummer;
	}

	private Postadress createPostAdress() {
		Postadress postadress = new Postadress();
		postadress.setValue("postadress");
		return postadress;
	}

	private Land createLand() {
		Land land = new Land();
		land.setValue("Land");
		return land;
	}

	private InternIdentitetsbeteckning createId() {
		InternIdentitetsbeteckning identitetsbeteckning = new InternIdentitetsbeteckning();
		identitetsbeteckning.setValue("ID");
		return identitetsbeteckning;
	}

	private Enhet createEnhet() {
		Enhet enhet = new Enhet();
		enhet.setId(createInternIdBeteckning());
		enhet.setKontaktuppgifter(createKontaktuppgifter());
		enhet.setNamn(createNamn());
		enhet.setPerson(createPerson());
		return enhet;
	}

	private Person createPerson() {
		Person person = new Person();
		person.setEfternamn("Nilsson");
		person.setFornamn("Nils");
		person.setId(createId());
		person.setKontaktuppgifter(createKontaktuppgifter());
		person.setNamn("Nils Nilsson");
		return person;
	}

	private Kontaktuppgifter createKontaktuppgifter() {
		Kontaktuppgifter kontaktuppgifter = new Kontaktuppgifter();
		kontaktuppgifter.setAdress(createAdress());
		kontaktuppgifter.setEpost(createEpotAdress());
		kontaktuppgifter.setTelefon(createTelefon());
		return kontaktuppgifter;
	}

	private InternIdentitetsbeteckning createInternIdBeteckning() {
		InternIdentitetsbeteckning identitetsbeteckning = new InternIdentitetsbeteckning();
		identitetsbeteckning.setValue("ID");
		return identitetsbeteckning;
	}

	private Avsandare createAvsandare() {
		Avsandare avsandare = new Avsandare();
		avsandare.setOrganisation(createOrganisation());
		avsandare.setReferens(createReferenceAdressering());
		return avsandare;
	}

	private ReferensAdressering createReferenceAdressering() {
		ReferensAdressering adressering = new ReferensAdressering();
		adressering.setValue("refadressing");
		return adressering;
	}
	
	private void setSenderId (String fkSenderId) {

    	// Get the underlying Client object from the proxy object of service interface
    	Client proxy = ClientProxy.getClient(_service);
    	 
    	// Creating HTTP headers
    	// Allow the mandatory headers to be null, i.e. skip setting them, 
    	// to be able to construct negative tests that verify error handling when one or both header are missing
    	Map<String, List<String>> headers = new HashMap<String, List<String>>();
    	if (fkSenderId != null) {
    		headers.put(FkAdapterUtil.X_FK_SENDER_ID, Arrays.asList(fkSenderId));
    	}
    	 
    	// Add HTTP headers to the web service request
    	proxy.getRequestContext().put(Message.PROTOCOL_HEADERS, headers);
    	 
//    	// If you want to log the SOAP XML of outgoing requests and incoming responses at client side, you can leave this uncommented. It'll be helpful in debugging.
//    	proxy.getOutInterceptors().add(new LoggingOutInterceptor());
//    	proxy.getInInterceptors().add(new LoggingInInterceptor());    	
    }
}
