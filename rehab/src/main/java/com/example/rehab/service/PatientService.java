package com.example.rehab.service;

import com.example.rehab.models.dto.PatientDTO;

import java.util.List;

public interface PatientService {

    /**
     * Finds all patients and convert them to dtos
     * @author Dmitriy Zhiburtovich
     */
    List<PatientDTO> findAll();

    /**
     * Convert dto to patient model and save it in repository.
     * @author Dmitriy Zhiburtovich
     */
    void createPatient(PatientDTO patientDTO);

    /**
     * Gets patient by id and convert it to dto
     * @author Dmitriy Zhiburtovich
     */
    PatientDTO getPatientByID(long id);

    /**
     * Gets id by patient name
     * @author Dmitriy Zhiburtovich
     */
    long getIdByName(String name);

    /**
     * Sets patient status to 'DISCHARGED' and cancels all active appointments
     * @author Dmitriy Zhiburtovich
     */
    void dischargePatient(long id);

    /**
     * Sets patient status to 'ON_TREATMENT'
     * @author Dmitriy Zhiburtovich
     */
    void returnPatient(long id);

    /**
     * Deletes patient from db
     * @author Dmitriy Zhiburtovich
     */
    void deletePatient(long id);

    /**
     * Finds all patients which belongs to authenticated doctor
     * @author Dmitriy Zhiburtovich
     */
    List<PatientDTO> getPatientsByDoctor(String doctor);

    /**
     * Checks if there are appointments which hasn't been completed or canceled
     * @author Dmitriy Zhiburtovich
     */
    boolean hasUnfinishedAppointments(long patientID);

}
