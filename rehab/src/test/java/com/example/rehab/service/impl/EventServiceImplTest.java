package com.example.rehab.service.impl;

import com.example.rehab.models.Event;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.DispatcherService;
import com.example.rehab.service.mapper.Mapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.jms.ConnectionFactory;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.rehab.models.enums.EventStatus.CANCELED;
import static com.example.rehab.models.enums.EventStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventServiceImplTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientServiceImpl patientService;

    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private Mapper mapper;

    @MockBean
    private DispatcherService dispatcherService;

    @MockBean
    private ConnectionFactory getConnectionFactory;

    AppointmentDTO testAppointment;

    long patientID;

    long appointmentID;

    long eventID;

    PatientDTO testPatient;

    EventDTO testEvent;

    @BeforeAll
    void setUp() {
        testPatient = new PatientDTO("PatientForTest", 0L,
                "Cold", "Doctor", null);
        patientService.createPatient(testPatient);
        patientID = patientService.getIdByName("PatientForTest");
        appointmentID = appointmentService.createAppointment(patientID, "Aerobics",new String[]{"4","5","6"},
                new String[]{"14:15","14:30","15:00"},"3","0", TypeOfAppointment.PROCEDURE);
        testEvent= eventService.findByPatient(patientID).get(0);
        eventID=testEvent.getId();


    }

    @Test
    @Order(1)
    void testAppointmentEventsAreCreated() {
        List<EventDTO> events = eventService.findByPatient(patientID);
        assertNotNull(events);
        assertEquals(27,events.size());
    }

    @Test
    @Order(2)
    void testFindAllRecentWorksCorrect() {
        List<EventDTO> events = eventService.findAll();
        List<EventDTO> recentEvents = eventService.findAllRecent();
        boolean containsRecentEvents = false;
        int counter = 0;
        LocalDateTime date = LocalDateTime.now();
        for (EventDTO event:events) {
            if ((event.getDate().getDayOfYear()==date.getDayOfYear())
                            && (event.getDate().getYear()==date.getYear())
            && (((date.getHour() == event.getDate().getHour()) && (date.getMinute() < event.getDate().getMinute()))
                    || (date.getHour()+1 == event.getDate().getHour()))) {
                containsRecentEvents = true;
                counter++;
            }
        }
        if (containsRecentEvents) {
            assertEquals(counter,recentEvents.size());
        } else {
            assertEquals(0,recentEvents.size());
        }
    }

    @Test
    @Order(3)
    void testFindAllTodayWorksCorrect() {
        List<EventDTO> events = eventService.findAll();
        List<EventDTO> todayEvents = eventService.findAllToday();
        boolean containsTodayEvents = false;
        int counter = 0;
        LocalDateTime date = LocalDateTime.now();
        for (EventDTO event:events) {
            if ((event.getDate().getDayOfYear()==date.getDayOfYear())
                    && ((event.getDate().getYear()==date.getYear())))
            {
                containsTodayEvents = true;
                counter++;
            }
        }
        if (containsTodayEvents) {
            assertEquals(counter,todayEvents.size());
        } else {
            assertEquals(0,todayEvents.size());
        }
    }

    @Test
    @Order(4)
    void testCompleteEvent() {
        if (eventService.isToday(testEvent)) {
            eventService.completeEvent(eventID);
            testEvent= eventService.findByPatient(patientID).get(0);
            assertEquals(COMPLETED,testEvent.getEventStatus());
        } else {
            assertThrows(IllegalStateException.class, ()-> eventService.completeEvent(eventID));
        }
        eventRepository.deleteById(eventID);
    }

    @Test
    @Order(5)
    void testCancelEvent() {
        testEvent= eventService.findByPatient(patientID).get(0);
        eventID=testEvent.getId();
        eventService.cancelEvent(eventID,"Test message");
        testEvent= eventService.findById(eventID);
        assertEquals(CANCELED,testEvent.getEventStatus());
        assertEquals("Test message", testEvent.getMessage());
    }

    @Test
    @Order(6)
    void testHideEvent() {
        eventService.hideEvent(eventID);
        testEvent= eventService.findById(eventID);
        assertFalse(testEvent.isActive());
        eventRepository.deleteById(eventID);
    }


    @AfterAll
    void tearDown() {
        appointmentService.cancelAppointment(appointmentID);
        appointmentService.deleteAppointment(appointmentID);
        patientService.deletePatient(patientID);
    }

}


