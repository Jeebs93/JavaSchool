package com.example.rehab.service.impl.unit;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.DispatcherService;
import com.example.rehab.service.EventService;
import com.example.rehab.service.impl.EventServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventServiceImplUnitTests {

    private final PatientRepository patientRepository = Mockito.mock(PatientRepository.class);
    private final AppointmentRepository appointmentRepository = Mockito.mock(AppointmentRepository.class);
    private final DispatcherService dispatcherService = Mockito.mock(DispatcherService.class);
    private final AppointmentService appointmentService = Mockito.mock(AppointmentService.class);
    private final Mapper mapper = new Mapper(new ModelMapper());
    private final EventRepository eventRepository = Mockito.mock(EventRepository.class);

    private final EventService eventService = new EventServiceImpl(mapper, eventRepository, patientRepository,
            appointmentRepository, dispatcherService);



    private final Patient testPatient = new Patient("Patient",123L,"doctor", PatientStatus.ON_TREATMENT);
    private final Appointment testAppointment = new Appointment(1L, testPatient, null,
            TypeOfAppointment.CURE, "Vitamin C", "On Monday at 13:30 for three weeks",
            3, 30, 0, true, false,false);

    private final List<Event> testEventList = Arrays.asList(
            new Event(2L,testPatient,testAppointment,LocalDateTime.now(),
                    EventStatus.PLANNED, TypeOfAppointment.CURE, true,""),
            new Event(3L,testPatient,testAppointment,LocalDateTime.now().plusDays(7),
                    EventStatus.PLANNED, TypeOfAppointment.CURE, true,""),
            new Event(4L,testPatient,testAppointment,LocalDateTime.now().plusDays(14),
                    EventStatus.PLANNED, TypeOfAppointment.CURE, true,"")
    );


    @BeforeAll
    void setUp() {
        testPatient.setId(5L);
        testAppointment.setEventList(testEventList);
    }

    @Test
    void testIsTodayWorksCorrect() {
        EventDTO eventDTO = new EventDTO(LocalDateTime.now());
        assertTrue(eventService.isToday(eventDTO));
        eventDTO.setDate(LocalDateTime.now().plusDays(1));
        assertFalse(eventService.isToday(eventDTO));
    }

    @Test
    void testFindAllWorksCorrect() {
        Mockito.when(eventRepository.findAllByActiveTrue()).thenReturn(testEventList);
        List<EventDTO> events = eventService.findAll();
        assertNotNull(events);
        assertEquals(3,events.size());
    }

    @Test
    void testFindAllTodayWorksCorrect() {
        Mockito.when(eventRepository.findAllByActiveTrue()).thenReturn(testEventList);
        List<EventDTO> events = eventService.findAllToday();
        assertNotNull(events);
        assertEquals(1,events.size());
        for (EventDTO event:events) {
            assertEquals(LocalDateTime.now().getDayOfYear(), event.getDate().getDayOfYear());
        }
    }

    @Test
    void testFindByPatient() {
        Mockito.when(eventRepository.findAllByActiveTrue()).thenReturn(testEventList);
        List<EventDTO> events = eventService.findByPatient(5);
        assertNotNull(events);
        assertEquals(3,events.size());
        for (EventDTO event:events) {
            assertEquals(testPatient, event.getPatient());
        }
    }

    @Test
    void testFindById() {
        Mockito.when(eventRepository.findEventById(2)).thenReturn(testEventList.get(0));
        EventDTO event = eventService.findById(2);
        assertNotNull(event);
    }

    @Test
    void testFindAllRecent() {
        testEventList.get(0).setDate(LocalDateTime.now().plusMinutes(10));
        Mockito.when(eventRepository.findAllByActiveTrue()).thenReturn(testEventList);
        List<EventDTO> events = eventService.findAllRecent();
        assertEquals(1,events.size());
    }

    @Test
    void testFindAllToday() {
        testEventList.get(1).setDate(LocalDateTime.now());
        Mockito.when(eventRepository.findAllByActiveTrue()).thenReturn(testEventList);
        List<EventDTO> events = eventService.findAllToday();
        assertEquals(2,events.size());
        testEventList.get(1).setDate(LocalDateTime.now().plusDays(7));
    }

    @Test
    void testHideEvents() {
        Mockito.when(appointmentRepository.getAppointmentById(1)).thenReturn(testAppointment);
        Mockito.when(eventRepository.findAllByAppointment(testAppointment)).thenReturn(testEventList);
        for (int i = 2; i < 5; i++) {
            Mockito.when(eventRepository.findEventById(i)).thenReturn(testEventList.get(i-2));
        }

        eventService.hideEvents(1);
        for (Event event:testEventList) {
            assertTrue(!event.isActive());
            event.setActive(true);
        }
    }

    @Test
    void testCompleteEvent() {
        long eventId = testEventList.get(0).getId();

        Mockito.when(eventRepository.findEventById(eventId)).thenReturn(testEventList.get(0));
        eventService.completeEvent(eventId);
        assertEquals(EventStatus.COMPLETED, testEventList.get(0).getEventStatus());
        testEventList.get(0).setEventStatus(EventStatus.PLANNED);
        Mockito.when(eventRepository.findEventById(3)).thenReturn(testEventList.get(1));
        assertThrows(IllegalStateException.class, ()-> eventService.completeEvent(3));
    }

    @Test
    void testCancelEvent() {
        long eventId = testEventList.get(0).getId();
        Event event = testEventList.get(0);

        Mockito.when(eventRepository.findEventById(eventId)).thenReturn(event);
        eventService.cancelEvent(eventId,"Test Message");
        assertEquals(EventStatus.CANCELED, event.getEventStatus());
        assertEquals("Test Message",event.getMessage());
        assertEquals(1,event.getAppointment().getCanceledEvents());
        testEventList.get(0).setEventStatus(EventStatus.PLANNED);
        testEventList.get(0).setMessage("");
        event.getAppointment().setCanceledEvents(0);
    }

    @Test
    void testHideEvent() {
        long eventId = testEventList.get(0).getId();
        Event event = testEventList.get(0);

        Mockito.when(eventRepository.findEventById(eventId)).thenReturn(event);
        eventService.hideEvent(eventId);
        assertFalse(event.isActive());
        event.setActive(true);
    }

    @Test
    void testDeleteEvents() {
        long appointmentID = testAppointment.getId();
        final AtomicInteger count = new AtomicInteger(0);
        Mockito.when(appointmentRepository.getAppointmentById(appointmentID)).thenReturn(testAppointment);
        Mockito.when(eventRepository.findAllByAppointment(testAppointment)).thenReturn(testEventList);
            Mockito.doAnswer(invocationOnMock -> {
                testEventList.get(count.get()).setActive(false);
                count.getAndIncrement();
                return null;
            }).when(eventRepository).delete(any(Event.class));

        eventService.deleteEvents(appointmentID);
        for (int i = 0; i < testEventList.size(); i++) {
            assertFalse(testEventList.get(i).isActive());
            testEventList.get(i).setActive(true);
        }
    }

    @Test
    void testCreateEvents() {
        AppointmentDTO appointmentDTO = mapper.convertAppointmentToDTO(testAppointment);
        appointmentDTO.setTime(Arrays.asList("13:30"));
        appointmentDTO.setWeekDays(Arrays.asList("1"));
        Mockito.when(patientRepository.getPatientById(appointmentDTO.getPatientId())).thenReturn(testPatient);
        Mockito.when(appointmentRepository.getAppointmentById(appointmentDTO.getId())).thenReturn(testAppointment);
        List<Event> events = new ArrayList<>();
        Mockito.doAnswer(invocationOnMock -> {
            events.addAll(invocationOnMock.getArgument(0));
            return null;
        }).when(eventRepository).saveAll(any(List.class));
        eventService.createEvents(appointmentDTO, appointmentDTO.getId(), 0);
        assertEquals(3,events.size());
    }


}


