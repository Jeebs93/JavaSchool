package com.example.rehab.service.impl;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.PatientService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentServiceImplTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    AppointmentDTO testAppointment;
    long patientID;
    PatientDTO testPatient;

    @BeforeAll
    void setUp() {
        testPatient = new PatientDTO("PatientForTest", 1234567L,
                "Cold", "Doctor", null);
        patientService.createPatient(testPatient);
        patientID = patientService.getIdByName("PatientForTest");



    }

    @AfterAll
    void tearDown() {
    }

    @Test
    void createAppointment() {
    }

    @Test
    void testTimePatternView() {

        assertEquals( "On Monday, Tuesday at 13:30 for 2 weeks.",
                AppointmentServiceImpl.getTimePatternView((new String[]{"1","2"}),
                Arrays.asList("13:30"), "2"));
        
        assertEquals( "On Wednesday at 14:00, 15:15 for 1 week.",
                AppointmentServiceImpl.getTimePatternView((new String[]{"3"}),
                        Arrays.asList("14:00","15:15"), "1"));

        assertEquals( "On Thursday, Friday at 11:15, 12:30, 13:45 for 8 weeks.",
                AppointmentServiceImpl.getTimePatternView((new String[]{"4","5"}),
                        Arrays.asList("11:15","12:30","13:45"), "8"));


    }

    @Test
    void updateAppointment() {
    }

    @Test
    void cancelAppointment() {
    }

    @Test
    void findAllByPatient() {
    }

    @Test
    void findAppointmentById() {
    }
}