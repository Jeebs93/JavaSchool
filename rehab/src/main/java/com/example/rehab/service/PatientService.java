package com.example.rehab.service;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final Mapper mapper;

    private final PatientRepository patientRepository;

    private final AppointmentRepository appointmentRepository;

    private final AppointmentService appointmentService;

    public List<PatientDTO> findAll() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(mapper::convertPatientToDTO)
                .collect(Collectors.toList());
    }

    public void createPatient(PatientDTO patientDTO) {
        patientDTO.setPatientStatus(PatientStatus.ON_TREATMENT);
        Patient patient = mapper.convertPatientToEntity(patientDTO);
        patientRepository.save(patient);
    }

    public PatientDTO getPatientDTObyID(long id) {
        Optional<Patient> patientByID = patientRepository.findById(id);
        PatientDTO result = new PatientDTO();
        if (patientByID.isPresent()) {
            result = mapper.convertPatientToDTO(patientByID.get());
        }
        return result;
    }

    public void dischargePatient(long id) {
        Patient patient = patientRepository.getPatientById(id);
        patient.setPatientStatus(PatientStatus.DISCHARGED);
        patientRepository.save(patient);
        List<Appointment> appointments = appointmentRepository.findAllByPatient(patient);
        for (Appointment appointment:appointments) {
            appointmentService.cancelAppointment(appointment.getId());
        }
    }


}
