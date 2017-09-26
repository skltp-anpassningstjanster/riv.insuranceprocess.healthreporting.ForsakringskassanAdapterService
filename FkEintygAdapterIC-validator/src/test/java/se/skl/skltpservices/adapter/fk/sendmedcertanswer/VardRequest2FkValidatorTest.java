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
package se.skl.skltpservices.adapter.fk.sendmedcertanswer;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.inera.ifv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v3.RegisterMedicalCertificateType;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.SendMedicalCertificateAnswerType;


public class VardRequest2FkValidatorTest {
    private static JAXBContext jaxbContext;
    private static VardRequest2FkValidator validator = new VardRequest2FkValidator();
    private SendMedicalCertificateAnswerType sendAnswerRequest;
    
    @BeforeClass
    public static void setupOnce() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(SendMedicalCertificateAnswerType.class);
    }

    @Before
    public void setup() throws Exception {
        String answer = IOUtils.toString(this.getClass().getResource("/giltigt-svar.xml"), "UTF-8");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        sendAnswerRequest =
            (SendMedicalCertificateAnswerType) ((JAXBElement) unmarshaller.unmarshal(new StringReader(answer))).getValue();
    }

    @Test
    public void testValidQuestion() throws Exception {
        validator.validateRequest(sendAnswerRequest);
    }
    
    @Test
    public void testMissingVardreferensId() throws Exception {
        sendAnswerRequest.getAnswer().setVardReferensId(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingAmne() throws Exception {
        sendAnswerRequest.getAnswer().setAmne(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyMeddelandeText() throws Exception {
        sendAnswerRequest.getAnswer().getFraga().setMeddelandeText("");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }

    @Test
    public void testMissingSigneringsTidpunkt() throws Exception {
        sendAnswerRequest.getAnswer().getFraga().setSigneringsTidpunkt(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }

    @Test
    public void testMissingAvsantTidpunkt() throws Exception {
        sendAnswerRequest.getAnswer().setAvsantTidpunkt(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }

    @Test
    public void testMissingPatient() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().setPatient(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPatientId() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().setPersonId(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPatientIdRoot() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().getPersonId().setRoot(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testWrongPatientIdRoot() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().getPersonId().setRoot("wrong");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPatientIdExtension() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().getPersonId().setExtension(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testInvalidPatientIdExtension() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().getPersonId().setExtension("121212-1212");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testPatientIdExtensionWithoutDashGetsCorrected() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().getPersonId().setExtension("191212121212");
        validator.validateRequest(sendAnswerRequest);
        Assert.assertEquals("19121212-1212", sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().getPersonId().getExtension());
    }

    @Test
    public void testMissingPatientName() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().setFullstandigtNamn(null);
        try {
            validator.validateRequest(sendAnswerRequest);
        } catch (Exception e) {
            Assert.fail("Missing patient name should be allowed");
        }
    }
    
    @Test
    public void testEmptyPatientName() throws Exception {
        sendAnswerRequest.getAnswer().getLakarutlatande().getPatient().setFullstandigtNamn("");
        try {
            validator.validateRequest(sendAnswerRequest);
        } catch (Exception e) {
            Assert.fail("Empty patient name should be allowed");
        }
    }
    
    @Test
    public void testMissingHosPersonal() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().setHosPersonal(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPersonalId() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().setPersonalId(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPersonalIdRoot() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getPersonalId().setRoot(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testWrongPersonalIdRoot() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getPersonalId().setRoot("wrong");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPersonalIdExtension() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getPersonalId().setExtension(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyPersonalIdExtension() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getPersonalId().setExtension("");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPersonalName() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().setFullstandigtNamn(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyPersonalName() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().setFullstandigtNamn("");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhet() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().setEnhet(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhetsId() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().setEnhetsId(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhetsIdRoot() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getEnhetsId().setRoot(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testWrongEnhetsIdRoot() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getEnhetsId().setRoot("wrong");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhetsIdExtension() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getEnhetsId().setExtension(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyEnhetsIdExtension() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getEnhetsId().setExtension("");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhetsIdName() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().setEnhetsnamn(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyEnhetsIdName() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().setEnhetsnamn("");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivare() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().setVardgivare(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivarId() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getVardgivare().setVardgivareId(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivarIdRoot() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getVardgivare().getVardgivareId().setRoot(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testWrongVardgivarIdRoot() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getVardgivare().getVardgivareId().setRoot("wrong");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivarIdExtension() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getVardgivare().getVardgivareId().setExtension(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyVardgivarIdExtension() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getVardgivare().getVardgivareId().setExtension("");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivarIdName() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getVardgivare().setVardgivarnamn(null);
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyVardgivarIdName() throws Exception {
        sendAnswerRequest.getAnswer().getAdressVard().getHosPersonal().getEnhet().getVardgivare().setVardgivarnamn("");
        try {
            validator.validateRequest(sendAnswerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
}
