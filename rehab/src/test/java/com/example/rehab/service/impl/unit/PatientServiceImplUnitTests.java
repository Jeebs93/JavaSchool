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

import java.util.ArrayList;
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

    private final Patient testPatient = new Patient("Patient",123L,"TestDoctor", PatientStatus.ON_TREATMENT);
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

    @Test
    void testPatientsByDoctor() {
        Mockito.when(patientRepository.getPatientsByDoctor("TestDoctor")).thenReturn(Arrays.asList(testPatient));
        List<PatientDTO> result = patientService.getPatientsByDoctor("TestDoctor");
        assertNotNull(result);
        assertEquals("TestDoctor",result.get(0).getDoctor());
    }

    @Test
    void getPatientsByName() {
        Mockito.when(patientRepository.getPatientsByName("TestPatient")).thenReturn(Arrays.asList(testPatient));
        List<PatientDTO> result = patientService.getPatientsByName("TestPatient");
        assertNotNull(result);
        assertEquals(PatientStatus.ON_TREATMENT,result.get(0).getPatientStatus());
    }

    @Test
    void testIsPatientAmbiguous() {
        List<Patient> patientList = new ArrayList<>();
        patientList.add(testPatient);
        patientList.add(testPatient);
        Mockito.when(patientRepository.getPatientsByName("TestPatient")).thenReturn(patientList);
        assertTrue(patientService.isPatientAmbiguous("TestPatient"));
        patientList.remove(0);
        assertFalse(patientService.isPatientAmbiguous("TestPatient"));

    }

    @Test
    void testGetPatientIDByName() {
        List<Patient> patientList = new ArrayList<>();
        patientList.add(testPatient);
        patientList.add(testPatient);
        patientList.get(0).setId(100L);
        Mockito.when(patientRepository.getPatientsByName("TestPatient")).thenReturn(patientList);
        assertEquals(100,patientService.getIdByName("TestPatient"));
        Mockito.when(patientRepository.getPatientsByName("TestPatient")).thenReturn(new ArrayList<Patient>());
        assertThrows(IndexOutOfBoundsException.class,() -> patientService.getIdByName("TestPatient"));
        patientList.get(0).setId(5L);
    }

    @Test
    void testGetPatientById() {
        Mockito.when(patientRepository.findById(patientID)).thenReturn(Optional.of(testPatient));
        PatientDTO result = patientService.getPatientByID(patientID);
        assertNotNull(result);
        assertEquals(patientID,result.getId());
    }

    @Test
    void testCreatePatient() {
        PatientDTO testDTO = new PatientDTO("TestPatient",123L,"TestDiagnosis","TestDoctor",null);
        testDTO.setId(1L);
        Mockito.doAnswer(invocationOnMock -> {
            testDTO.setPatientStatus(PatientStatus.ON_TREATMENT);
            return null;
        }).when(patientRepository).save(any(Patient.class));
        Mockito.when(patientRepository.getPatientByInsuranceNumber(testDTO.getInsuranceNumber())).thenReturn(testPatient);
        patientService.createPatient(testDTO);
        assertEquals(PatientStatus.ON_TREATMENT,testPatient.getPatientStatus());
    }

}
