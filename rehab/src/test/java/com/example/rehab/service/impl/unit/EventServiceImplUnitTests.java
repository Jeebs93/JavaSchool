package com.example.rehab.service.impl.unit;

import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.DispatcherService;
import com.example.rehab.service.impl.EventServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplUnitTests {


    Mapper mapper = Mockito.mock(Mapper.class);

    EventRepository eventRepository = Mockito.mock(EventRepository.class);

    PatientRepository patientRepository = Mockito.mock(PatientRepository.class);

    AppointmentRepository appointmentRepository = Mockito.mock(AppointmentRepository.class);

    DispatcherService dispatcherService = Mockito.mock(DispatcherService.class);

    @Autowired
    private EventServiceImpl eventService = new EventServiceImpl(mapper, eventRepository, patientRepository,
            appointmentRepository, dispatcherService);





    @Test
    void testIsTodayWorksCorrect() {
        EventDTO eventDTO = new EventDTO(LocalDateTime.now());
        assertTrue(eventService.isToday(eventDTO));
        eventDTO.setDate(LocalDateTime.now().plusDays(1));
        assertFalse(eventService.isToday(eventDTO));
    }

}
