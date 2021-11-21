package com.example.rehab.service;

import com.example.rehab.models.dto.PatientDTO;

import java.util.List;

public interface PatientService {

    List<PatientDTO> findAll();

    void createPatient(PatientDTO patientDTO);

    PatientDTO getPatientByID(long id);

    long getIdByName(String name);

    void dischargePatient(long id);

    void returnPatient(long id);

    void deletePatient(long id);

    List<PatientDTO> getPatientsByDoctor(String doctor);

    boolean hasUnfinishedAppointments(long patientID);

}
