package com.example.rehab.service.impl.unit;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.service.DispatcherService;
import com.example.rehab.service.impl.AppointmentServiceImpl;
import com.example.rehab.service.impl.EventServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import org.hibernate.MappingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.rehab.models.enums.EventStatus.COMPLETED;
import static com.example.rehab.models.enums.EventStatus.PLANNED;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentServiceImplUnitTests {

    private final Mapper mapper = new Mapper(new ModelMapper());
    private final AppointmentRepository appointmentRepository = Mockito.mock(AppointmentRepository.class);
    private final EventServiceImpl eventService = Mockito.mock(EventServiceImpl.class);
    private final EventRepository eventRepository = Mockito.mock(EventRepository.class);
    private final DispatcherService dispatcherService = Mockito.mock(DispatcherService.class);
    private final AppointmentServiceImpl appointmentService = new AppointmentServiceImpl(mapper,appointmentRepository,
            eventService,eventRepository,dispatcherService);

    private final Patient testPatient = new Patient("Patient",123L,"doctor", PatientStatus.ON_TREATMENT);
    private Appointment testAppointment = new Appointment(1L, testPatient, null,
            TypeOfAppointment.CURE, "Aerobics", "On Thursday, Friday, Saturday at 14:15, 14:30, 15:00 for 3 weeks.",
            3, 0, 0, true, false,false);

    private List<Event> testEventList = Arrays.asList(
            new Event(2L,testPatient,testAppointment, LocalDateTime.now(),
                    EventStatus.PLANNED, TypeOfAppointment.CURE, true,""),
            new Event(3L,testPatient,testAppointment,LocalDateTime.now().plusDays(7),
                    EventStatus.PLANNED, TypeOfAppointment.CURE, true,""),
            new Event(4L,testPatient,testAppointment,LocalDateTime.now().plusDays(14),
                    EventStatus.PLANNED, TypeOfAppointment.CURE, true,"")
    );



    @BeforeAll
    void setUp() {
        testPatient.setId(5L);
    }

    @BeforeEach
    void setUpEach() {
        testAppointment = new Appointment(1L, testPatient, null,
                TypeOfAppointment.CURE, "Aerobics", "On Thursday, Friday, Saturday at 14:15, 14:30, 15:00 for 3 weeks.",
                3, 0, 0, true, false,false);

        testEventList = Arrays.asList(
                new Event(2L,testPatient,testAppointment, LocalDateTime.now(),
                        EventStatus.PLANNED, TypeOfAppointment.CURE, true,""),
                new Event(3L,testPatient,testAppointment,LocalDateTime.now().plusDays(7),
                        EventStatus.PLANNED, TypeOfAppointment.CURE, true,""),
                new Event(4L,testPatient,testAppointment,LocalDateTime.now().plusDays(14),
                        EventStatus.PLANNED, TypeOfAppointment.CURE, true,"")
        );
    }

    @Test
    void testCreateAppointment() {
        Mockito.doAnswer(invocationOnMock -> {
            Appointment appointment = invocationOnMock.getArgument(0);
            appointment.setId(1L);
            assertEquals("Aerobics", appointment.getValue());
            assertEquals("On Thursday, Friday, Saturday at 14:15, 14:30, 15:00 for 3 weeks.", appointment.getTimePattern());
            assertEquals(3, appointment.getPeriod());
            assertEquals(0, appointment.getDose());
            return null;
        }).when(appointmentRepository).save(any(Appointment.class));
        appointmentService.createAppointment(testPatient.getId(), "Aerobics", new String[]{"4", "5", "6"},
                new String[]{"14:15", "14:30", "15:00"}, "3", "0", TypeOfAppointment.PROCEDURE);
    }

    @Test
    void testUpdateAppointment() {
        Mockito.when(appointmentRepository.getAppointmentById(1)).thenReturn(testAppointment);
        Mockito.doAnswer(invocationOnMock -> {
            Appointment appointment = invocationOnMock.getArgument(0);
            assertEquals("On Monday, Tuesday at 16:00, 16:30 for 4 weeks.",appointment.getTimePattern());
            assertEquals(4,appointment.getPeriod());
            return null;
        }).when(appointmentRepository).save(any(Appointment.class));
        appointmentService.updateAppointment(1,new String[] {"1","2"},new String[]{"16:00", "16:30"},
                "4","0");
    }

    @Test
    void testCancelAppointment() {
        Mockito.when(appointmentRepository.getAppointmentById(1)).thenReturn(testAppointment);
        appointmentService.cancelAppointment(1);
        assertTrue(testAppointment.isCanceled());
        assertFalse(testAppointment.isActive());
    }

    @Test
    void testHideAppointment() {
        Mockito.when(appointmentRepository.getAppointmentById(1)).thenReturn(testAppointment);
        appointmentService.hideAppointment(1);
        assertFalse(testAppointment.isActive());
    }

    @Test
    void testCompleteAppointment() {
        Mockito.when(appointmentRepository.getAppointmentById(1)).thenReturn(testAppointment);
        appointmentService.completeAppointment(1);
        assertFalse(testAppointment.isCompleted());
    }

    @Test
    void testCheckAppointmentStatus(){
        AppointmentDTO appointmentDTO = mapper.convertAppointmentToDTO(testAppointment);
        List<AppointmentDTO> list = Arrays.asList(appointmentDTO);
        Mockito.when(eventRepository.findAllByAppointment(any(Appointment.class))).thenReturn(testEventList);
        Mockito.when(appointmentRepository.getAppointmentById(1)).thenReturn(testAppointment);
        appointmentService.checkAppointmentsStatus(list);
        assertFalse(testAppointment.isCompleted());

        for (Event event:testEventList) {
            event.setEventStatus(COMPLETED);
        }
        appointmentService.checkAppointmentsStatus(list);
        assertTrue(testAppointment.isCompleted());
    }


}

