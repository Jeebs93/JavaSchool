package com.example.rehab.service.impl.unit;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.impl.AppointmentServiceImpl;
import com.example.rehab.service.impl.PatientServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientServiceImplUnitTests {

    private final Mapper mapper = new Mapper(new ModelMapper());
    private final PatientRepository patientRepository = Mockito.mock(PatientRepository.class);
    private final AppointmentRepository appointmentRepository = Mockito.mock(AppointmentRepository.class);
    private final AppointmentServiceImpl appointmentService = Mockito.mock(AppointmentServiceImpl.class);
    private final PatientServiceImpl patientService = new PatientServiceImpl(mapper, patientRepository,
            appointmentRepository, appointmentService);

    private final Patient testPatient = new Patient("Patient",123L,"doctor", PatientStatus.ON_TREATMENT);
    private final Appointment testAppointment1 = new Appointment(1L, testPatient, null,
            TypeOfAppointment.CURE, "Vitamin C", "On Monday at 13:30 for three weeks",
            3, 30, 0, true, false,false);
    private final Appointment testAppointment2 = new Appointment(2L, testPatient, null,
            TypeOfAppointment.CURE, "Korvalol", "On Monday at 13:30 for three weeks",
            3, 30, 0, true, false,false);
    private final Appointment testAppointment3 = new Appointment(3L, testPatient, null,
            TypeOfAppointment.CURE, "Aspirin", "On Monday at 13:30 for three weeks",
            3, 30, 0, true, false,false);
    private final List<Appointment> testAppointmentList = Arrays.asList(testAppointment1,testAppointment2,testAppointment3);
    long patientID = 5L;


    @BeforeAll
    void setUp() {
        testAppointmentList.forEach(appointment -> appointment.setCanceled(false));
        testAppointmentList.forEach(appointment -> appointment.setCompleted(false));
        testPatient.setId(patientID);
        testPatient.setAppointmentList(testAppointmentList);
    }

    @Test
    void testDeletePatient() {
        Mockito.doAnswer(invocationOnMock -> {
            testPatient.setPatientStatus(PatientStatus.DISCHARGED);
            return null;
        }).when(patientRepository).deleteById(any(Long.class));
        patientService.deletePatient(5L);
        assertEquals(PatientStatus.DISCHARGED, testPatient.getPatientStatus());
        testPatient.setPatientStatus(PatientStatus.ON_TREATMENT);
    }

    @Test
    void testReturnPatient() {
        Mockito.when(patientRepository.getPatientById(patientID)).thenReturn(testPatient);
        testPatient.setPatientStatus(PatientStatus.DISCHARGED);
        patientService.returnPatient(patientID);
        assertEquals(PatientStatus.ON_TREATMENT,testPatient.getPatientStatus());
    }

    @Test
    void testHasUnfinishedAppointments() {
        PatientDTO patientDTO = mapper.convertPatientToDTO(testPatient);
        Mockito.when(patientRepository.findById(patientID)).thenReturn(Optional.of(testPatient));
        Mockito.when(appointmentService.findAllByPatient(patientDTO)).thenReturn(testAppointmentList.stream()
                .map(mapper::convertAppointmentToDTO)
                .collect(Collectors.toList()));
        assertTrue(patientService.hasUnfinishedAppointments(patientID));

        testAppointmentList.forEach(appointment -> appointment.setCompleted(true));
        Mockito.when(appointmentService.findAllByPatient(patientDTO)).thenReturn(testAppointmentList.stream()
                .map(mapper::convertAppointmentToDTO)
                .collect(Collectors.toList()));
        assertFalse(patientService.hasUnfinishedAppointments(patientID));
        testAppointmentList.forEach(appointment -> appointment.setCompleted(false));
    }

    @Test
    void testDischargePatient() {
        Mockito.when(patientRepository.getPatientById(patientID)).thenReturn(testPatient);
        Mockito.when(appointmentRepository.findAllByPatient(testPatient)).thenReturn(testPatient.getAppointmentList());
        patientService.dischargePatient(patientID);
        assertEquals(PatientStatus.DISCHARGED,testPatient.getPatientStatus());
        testPatient.setPatientStatus(PatientStatus.ON_TREATMENT);
    }

}
