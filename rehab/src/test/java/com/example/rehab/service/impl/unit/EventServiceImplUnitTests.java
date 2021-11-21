package com.example.rehab.service.impl.unit;

import com.example.rehab.models.Event;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.DispatcherService;
import com.example.rehab.service.EventService;
import com.example.rehab.service.impl.EventServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventServiceImplUnitTests {




    private PatientRepository patientRepository = Mockito.mock(PatientRepository.class);

    private AppointmentRepository appointmentRepository = Mockito.mock(AppointmentRepository.class);

    private DispatcherService dispatcherService = Mockito.mock(DispatcherService.class);

    private AppointmentService appointmentService = Mockito.mock(AppointmentService.class);

    private Mapper mapper = new Mapper(new ModelMapper());



    private EventRepository eventRepository = Mockito.mock(EventRepository.class);

    private EventService eventService = new EventServiceImpl(mapper, eventRepository, patientRepository,
            appointmentRepository, dispatcherService);



    @Test
    void testIsTodayWorksCorrect() {
        EventDTO eventDTO = new EventDTO(LocalDateTime.now());
        assertTrue(eventService.isToday(eventDTO));
        eventDTO.setDate(LocalDateTime.now().plusDays(1));
        assertFalse(eventService.isToday(eventDTO));
    }

    @Test
    void testFindAllWorksCorrect() {
        List<EventDTO> events = eventService.findAll();
        assertNotNull(events);
    }

    @Test
    void testFindAllTodayWorksCorrect() {
        List<EventDTO> events = eventService.findAllToday();
        assertNotNull(events);
        for (EventDTO event:events) {
            assertEquals(LocalDateTime.now().getDayOfYear(), event.getDate().getDayOfYear());
        }
    }



}
