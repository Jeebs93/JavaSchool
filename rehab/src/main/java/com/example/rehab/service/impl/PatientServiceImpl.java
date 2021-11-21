package com.example.rehab.service.impl;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.PatientService;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.AspectJAdviceParameterNameDiscoverer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final Mapper mapper;

    private final PatientRepository patientRepository;

    private final AppointmentRepository appointmentRepository;

    private final AppointmentServiceImpl appointmentService;

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
        log.info("Patient has been created");
    }

    public PatientDTO getPatientByID(long id) {
        Optional<Patient> patientByID = patientRepository.findById(id);
        PatientDTO result = new PatientDTO();
        if (patientByID.isPresent()) {
            result = mapper.convertPatientToDTO(patientByID.get());
        }
        return result;
    }

    public long getIdByName(String name) {

        List<Patient> patients = patientRepository.getPatientsByName(name);
        return patients.get(0).getId();
    }

    public boolean isPatientAmbiguous(String name) {
        return patientRepository.getPatientsByName(name).size() > 1;
    }

    public List<PatientDTO> getPatientsByName(String name) {
        List<Patient> patients = patientRepository.getPatientsByName(name);
        return patients.stream()
                .map(mapper::convertPatientToDTO)
                .collect(Collectors.toList());
    }

    public List<PatientDTO> getPatientsByDoctor(String doctor) {
        List<Patient> patients = patientRepository.getPatientsByDoctor(doctor);
        return patients.stream()
                .map(mapper::convertPatientToDTO)
                .collect(Collectors.toList());
    }

    public void dischargePatient(long id) {
        Patient patient = patientRepository.getPatientById(id);
        patient.setPatientStatus(PatientStatus.DISCHARGED);
        patientRepository.save(patient);
        log.info("Patient has been discharged");
        List<Appointment> appointments = appointmentRepository.findAllByPatient(patient);
        for (Appointment appointment:appointments) {
            appointmentService.cancelAppointment(appointment.getId());
        }
    }

    public boolean hasUnfinishedAppointments(long patientID) {
        PatientDTO patientDTO = getPatientByID(patientID);
        List<AppointmentDTO> appointments = appointmentService.findAllByPatient(patientDTO);
        boolean result = false;

        for (AppointmentDTO appointment:appointments) {
            if (!appointment.isCompleted() && !appointment.isCanceled()) {
                result=true;
                break;
            }
        }
        return result;
    }

    public void returnPatient(long id) {
        Patient patient = patientRepository.getPatientById(id);
        patient.setPatientStatus(PatientStatus.ON_TREATMENT);
        patientRepository.save(patient);
        log.info("Patient has been returned to treatment");
    }

    public void deletePatient(long id) {
        patientRepository.deleteById(id);
        log.info("Patient has been deleted");
    }



}
