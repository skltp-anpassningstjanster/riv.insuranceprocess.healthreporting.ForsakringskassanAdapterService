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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;

import se.fk.vardgivare.sjukvard.taemotlakarintygresponder.v1.TaEmotLakarintygType;


public class Vard2FkTransformerTest {

    private Vard2FkTransformer transformer = new Vard2FkTransformer();
    private XMLInputFactory factory = XMLInputFactory.newInstance();
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private XMLStreamReader streamReader;
    private MuleMessage message;

    @Before
    public void setup() throws Exception {
        jaxbContext = JAXBContext.newInstance( TaEmotLakarintygType.class );
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        XMLUnit.setIgnoreWhitespace(false);
        XMLUnit.setNormalizeWhitespace(false);
        message = Mockito.mock(MuleMessage.class);
    }
    
    @Test
    public void testValid() throws Exception {
        String certificate = IOUtils.toString(this.getClass().getResource("/fk7263_max.xml"), "UTF-8");
        streamReader = factory.createXMLStreamReader(new StringReader(certificate));
        Mockito.when(message.getPayload()).thenReturn(new Object[] {null, streamReader});
        
        Object[] result = (Object[]) transformer.transformMessage(message, "UTF-8");
        TaEmotLakarintygType taEmotLakarintygType = (TaEmotLakarintygType) result[1];
        StringWriter writer = new StringWriter();
        JAXBElement<TaEmotLakarintygType> jaxbElement = new JAXBElement<TaEmotLakarintygType>(new QName("TaEmotLakarintygType"), TaEmotLakarintygType.class, taEmotLakarintygType);
        marshaller.marshal(jaxbElement, writer);
        String expected = IOUtils.toString(this.getClass().getResource("/fk7263_max_transformed.xml"), "UTF-8");
        Diff diff = new Diff(expected, writer.toString());
        Assert.assertTrue(diff.toString(), diff.identical()); 

    }
    
    @Test(expected=TransformerException.class)
    public void testInvalidDateTime() throws Exception {
        String certificate = IOUtils.toString(this.getClass().getResource("/fk7263_invalid_dateTime.xml"), "UTF-8");
        streamReader = factory.createXMLStreamReader(new StringReader(certificate));
        Mockito.when(message.getPayload()).thenReturn(new Object[] {null, streamReader});
        
        Object[] result = (Object[]) transformer.transformMessage(message, "UTF-8");
        TaEmotLakarintygType taEmotLakarintygType = (TaEmotLakarintygType) result[1];
        StringWriter writer = new StringWriter();
        JAXBElement<TaEmotLakarintygType> jaxbElement = new JAXBElement<TaEmotLakarintygType>(new QName("TaEmotLakarintygType"), TaEmotLakarintygType.class, taEmotLakarintygType);
        marshaller.marshal(jaxbElement, writer);
        System.err.println(writer.toString());
    }
    
    @Test
    public void testDateInsteadOfDateTimeIsAccepted() throws Exception {
        String certificate = IOUtils.toString(this.getClass().getResource("/fk7263_date_instead_of_dateTime.xml"), "UTF-8");
        streamReader = factory.createXMLStreamReader(new StringReader(certificate));
        Mockito.when(message.getPayload()).thenReturn(new Object[] {null, streamReader});
        
        Object[] result = (Object[]) transformer.transformMessage(message, "UTF-8");
        TaEmotLakarintygType taEmotLakarintygType = (TaEmotLakarintygType) result[1];
        StringWriter writer = new StringWriter();
        JAXBElement<TaEmotLakarintygType> jaxbElement = new JAXBElement<TaEmotLakarintygType>(new QName("TaEmotLakarintygType"), TaEmotLakarintygType.class, taEmotLakarintygType);
        marshaller.marshal(jaxbElement, writer);
        String expected = IOUtils.toString(this.getClass().getResource("/fk7263_date_instead_of_dateTime_transformed.xml"), "UTF-8");
        Diff diff = new Diff(expected, writer.toString());
        Assert.assertTrue(diff.toString(), diff.identical()); 
    }
    
    @Test
    public void testDateTimeInsteadOfDateIsDiscarded() throws Exception {
        String certificate = IOUtils.toString(this.getClass().getResource("/fk7263_dateTime_instead_of_date.xml"), "UTF-8");
        streamReader = factory.createXMLStreamReader(new StringReader(certificate));
        Mockito.when(message.getPayload()).thenReturn(new Object[] {null, streamReader});
        
        Object[] result = (Object[]) transformer.transformMessage(message, "UTF-8");
        TaEmotLakarintygType taEmotLakarintygType = (TaEmotLakarintygType) result[1];
        StringWriter writer = new StringWriter();
        JAXBElement<TaEmotLakarintygType> jaxbElement = new JAXBElement<TaEmotLakarintygType>(new QName("TaEmotLakarintygType"), TaEmotLakarintygType.class, taEmotLakarintygType);
        marshaller.marshal(jaxbElement, writer);
        String expected = IOUtils.toString(this.getClass().getResource("/fk7263_max_transformed.xml"), "UTF-8");
        Diff diff = new Diff(expected, writer.toString());
        Assert.assertTrue(diff.toString(), diff.identical()); 
    }
    
    @Test
    public void testSecondFractionsAreDiscarded() throws Exception {
        String certificate = IOUtils.toString(this.getClass().getResource("/fk7263_max_secondFractions.xml"), "UTF-8");
        streamReader = factory.createXMLStreamReader(new StringReader(certificate));
        Mockito.when(message.getPayload()).thenReturn(new Object[] {null, streamReader});
        
        Object[] result = (Object[]) transformer.transformMessage(message, "UTF-8");
        TaEmotLakarintygType taEmotLakarintygType = (TaEmotLakarintygType) result[1];
        StringWriter writer = new StringWriter();
        JAXBElement<TaEmotLakarintygType> jaxbElement = new JAXBElement<TaEmotLakarintygType>(new QName("TaEmotLakarintygType"), TaEmotLakarintygType.class, taEmotLakarintygType);
        marshaller.marshal(jaxbElement, writer);
        String expected = IOUtils.toString(this.getClass().getResource("/fk7263_max_transformed.xml"), "UTF-8");
        Diff diff = new Diff(expected, writer.toString());
        Assert.assertTrue(diff.toString(), diff.identical()); 
    }
    
    @Test
    public void testTimeZoneIsAcceptedButNotRetained() throws Exception {
        String certificate = IOUtils.toString(this.getClass().getResource("/fk7263_max_timezone.xml"), "UTF-8");
        streamReader = factory.createXMLStreamReader(new StringReader(certificate));
        Mockito.when(message.getPayload()).thenReturn(new Object[] {null, streamReader});
        
        Object[] result = (Object[]) transformer.transformMessage(message, "UTF-8");
        TaEmotLakarintygType taEmotLakarintygType = (TaEmotLakarintygType) result[1];
        StringWriter writer = new StringWriter();
        JAXBElement<TaEmotLakarintygType> jaxbElement = new JAXBElement<TaEmotLakarintygType>(new QName("TaEmotLakarintygType"), TaEmotLakarintygType.class, taEmotLakarintygType);
        marshaller.marshal(jaxbElement, writer);
        String expected = IOUtils.toString(this.getClass().getResource("/fk7263_max_timezone_transformed.xml"), "UTF-8");
        Diff diff = new Diff(expected, writer.toString());
        Assert.assertTrue(diff.toString(), diff.identical()); 
    }
}
