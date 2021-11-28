package com.example.rehab.service.impl;

import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.service.AppointmentService;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentServiceImplTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientServiceImpl patientService;

    @Autowired
    private EventServiceImpl eventService;

    @MockBean
    private DispatcherService dispatcherService;

    @MockBean
    private ConnectionFactory getConnectionFactory;

    AppointmentDTO testAppointment;

    private EventServiceImplTest testService = new EventServiceImplTest();

    long patientID;

    long appointmentID;

    PatientDTO testPatient;

    @BeforeAll
    void setUp() {
        testPatient = new PatientDTO("PatientForTest", 0L,
                "Cold", "Doctor", null);
        patientService.createPatient(testPatient);
        patientID = patientService.getIdByName("PatientForTest");
        appointmentID = appointmentService.createAppointment(patientID, "Aerobics",new String[]{"4","5","6"},
                new String[]{"14:15","14:30","15:00"},"3","0", TypeOfAppointment.PROCEDURE);



    }

    @AfterAll
    void tearDown() {
       appointmentService.deleteAppointment(appointmentID);
       patientService.deletePatient(patientID);
    }

    @Test
    @Order(1)
    void testAppointmentIsCreated() {
        testAppointment = appointmentService.findAppointmentById(appointmentID);
        assertNotNull(testAppointment);
    }


    @Test
    @Order(2)
    void testAppointmentEventsAreCreated() {
        List<EventDTO> events = eventService.findByPatient(patientID);
        assertNotNull(events);
        assertEquals(27,events.size());
    }

    @Test
    @Order(3)
    void testAppointmentDataIsCorrect() {
        assertEquals(patientID,testAppointment.getPatientId());
        assertEquals("Aerobics",testAppointment.getValue());
        assertEquals("On Thursday, Friday, Saturday at 14:15, 14:30, 15:00 for 3 weeks.", testAppointment.getTimePattern());
        assertEquals(3,testAppointment.getPeriod());
        assertEquals("0",testAppointment.getDose());
        assertEquals(TypeOfAppointment.PROCEDURE,testAppointment.getTypeOfAppointment());
    }



    @Test
    @Order(4)
    void testAppointmentUpdated() {
        appointmentService.updateAppointment(appointmentID,new String[]{"1","2"},new String[]{"14:30"},"2","0");
        testAppointment = appointmentService.findAppointmentById(appointmentID);
        assertEquals("On Monday, Tuesday at 14:30 for 2 weeks.", testAppointment.getTimePattern());
        assertEquals(2,testAppointment.getPeriod());

    }

    @Test
    @Order(5)
    void testAppointmentEventsUpdated() {
        List<EventDTO> eventsUpdate = eventService.findByPatient(patientID);
        assertEquals(4,eventsUpdate.size());
    }

    @Test
    @Order(6)
    void testCancelAppointment() {
        appointmentService.cancelAppointment(appointmentID);
        testAppointment = appointmentService.findAppointmentById(appointmentID);
        assertFalse(testAppointment.isActive());
        List<EventDTO> eventsCanceled = eventService.findByPatient(patientID);
        assertEquals(0,eventsCanceled.size());
    }

}


