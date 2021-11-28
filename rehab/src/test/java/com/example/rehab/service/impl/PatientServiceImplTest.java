package com.example.rehab.service.impl;

import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.service.DispatcherService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.jms.ConnectionFactory;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientServiceImplTest {

    @MockBean
    private DispatcherService dispatcherService;

    @MockBean
    private ConnectionFactory getConnectionFactory;

    @Autowired
    private PatientServiceImpl patientService;

    long patientID;

    PatientDTO testPatient;

    @BeforeAll
     void setUp() {
        testPatient = new PatientDTO("PatientForTest", 0L,
                "Cold", "Doctor", null);
        patientService.createPatient(testPatient);
        patientID = patientService.getIdByName("PatientForTest");
    }

    @AfterAll
    void tearDown() {
        patientService.deletePatient(patientID);
    }

    @Test
    void testPatientList_NotNull() {
        List<PatientDTO> patients = patientService.findAll();
        assertNotNull(patients);
    }

    @Test
    void testPatientIsCreated() {
        PatientDTO patientDTO = patientService.getPatientByID(patientService.getIdByName("PatientForTest"));
        assertNotNull(patientDTO);
    }

    @Test
    void testPatientIsDischarged() {
        patientService.dischargePatient(patientID);
        testPatient = patientService.getPatientByID(patientID);
        assertEquals(PatientStatus.DISCHARGED, testPatient.getPatientStatus());
    }

    @Test
    void testPatientIsReturned() {
        patientService.returnPatient(patientID);
        testPatient = patientService.getPatientByID(patientID);
        assertEquals(PatientStatus.ON_TREATMENT, testPatient.getPatientStatus());
    }
}