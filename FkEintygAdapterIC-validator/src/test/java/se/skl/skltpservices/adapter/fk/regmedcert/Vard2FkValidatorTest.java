/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 *                          <http://cehis.se/>
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

public class Vard2FkValidatorTest {

    private static JAXBContext jaxbContext;
    private static Vard2FkValidator validator = new Vard2FkValidator();
    private RegisterMedicalCertificateType registerRequest;
    
    @BeforeClass
    public static void setupOnce() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(RegisterMedicalCertificateType.class);
    }

    @Before
    public void setup() throws Exception {
        String certificate = IOUtils.toString(this.getClass().getResource("/fk7263_max.xml"), "UTF-8");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        registerRequest =
            (RegisterMedicalCertificateType) ((JAXBElement) unmarshaller.unmarshal(new StringReader(certificate))).getValue();
    }
    
    @Test
    public void testValidCertificate() throws Exception {
        validator.validateRequest(registerRequest);
    }
    
    @Test
    public void testMissingPatient() throws Exception {
        registerRequest.getLakarutlatande().setPatient(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPatientId() throws Exception {
        registerRequest.getLakarutlatande().getPatient().setPersonId(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPatientIdRoot() throws Exception {
        registerRequest.getLakarutlatande().getPatient().getPersonId().setRoot(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testWrongPatientIdRoot() throws Exception {
        registerRequest.getLakarutlatande().getPatient().getPersonId().setRoot("wrong");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPatientIdExtension() throws Exception {
        registerRequest.getLakarutlatande().getPatient().getPersonId().setExtension(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testInvalidPatientIdExtension() throws Exception {
        registerRequest.getLakarutlatande().getPatient().getPersonId().setExtension("121212-1212");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testPatientIdExtensionWithoutDashGetsCorrected() throws Exception {
        registerRequest.getLakarutlatande().getPatient().getPersonId().setExtension("191212121212");
        validator.validateRequest(registerRequest);
        Assert.assertEquals("19121212-1212", registerRequest.getLakarutlatande().getPatient().getPersonId().getExtension());
    }
    
    @Test
    public void testMissingPatientName() throws Exception {
        registerRequest.getLakarutlatande().getPatient().setFullstandigtNamn(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyPatientName() throws Exception {
        registerRequest.getLakarutlatande().getPatient().setFullstandigtNamn("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingSkapadAvHosPersonal() throws Exception {
        registerRequest.getLakarutlatande().setSkapadAvHosPersonal(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPersonalId() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().setPersonalId(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPersonalIdRoot() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getPersonalId().setRoot(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testWrongPersonalIdRoot() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getPersonalId().setRoot("wrong");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPersonalIdExtension() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getPersonalId().setExtension(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyPersonalIdExtension() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getPersonalId().setExtension("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingPersonalName() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().setFullstandigtNamn(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyPersonalName() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().setFullstandigtNamn("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhet() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().setEnhet(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhetsId() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().setEnhetsId(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhetsIdRoot() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getEnhetsId().setRoot(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testWrongEnhetsIdRoot() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getEnhetsId().setRoot("wrong");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhetsIdExtension() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getEnhetsId().setExtension(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyEnhetsIdExtension() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getEnhetsId().setExtension("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingEnhetsIdName() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().setEnhetsnamn(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyEnhetsIdName() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().setEnhetsnamn("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivare() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().setVardgivare(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivarId() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare().setVardgivareId(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivarIdRoot() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare().getVardgivareId().setRoot(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testWrongVardgivarIdRoot() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare().getVardgivareId().setRoot("wrong");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivarIdExtension() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare().getVardgivareId().setExtension(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyVardgivarIdExtension() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare().getVardgivareId().setExtension("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testMissingVardgivarIdName() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare().setVardgivarnamn(null);
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyVardgivarIdName() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().getVardgivare().setVardgivarnamn("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyPostadress() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().setPostadress("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyPostnummer() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().setPostnummer("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyPostort() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().setPostort("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
    
    @Test
    public void testEmptyTelefonnummer() throws Exception {
        registerRequest.getLakarutlatande().getSkapadAvHosPersonal().getEnhet().setTelefonnummer("");
        try {
            validator.validateRequest(registerRequest);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // Expected
            Assert.assertTrue("", e.getMessage().contains("Validation error"));
        }
    }
}
