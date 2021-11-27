package com.example.rehab.service;

import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.TypeOfAppointment;

import java.util.List;

public interface AppointmentService {

    /**
     * Creates and saves appointment in database.
     * @param id - patient id, gets from url path.
     * @param value - name of appointment
     * @param weekdays - array of string digits. Each one represents day of week.
     * @param time - time array as a string. Format - hh:mm
     * @param period - appointment's number of weeks
     * @param dose - appointment's dose. If type is a procedure, automatically sets to zero.
     * @param typeOfAppointment - can be procedure or cure
     * @return id of appointment that has been created
     * @author Dmitriy Zhiburtovich
     */
    long createAppointment(long id, String value, String[] weekdays, String[] time,
                           String period, String dose, TypeOfAppointment typeOfAppointment);

    /**
     * Updates existing appointment's time pattern and dose. Deletes all planned events and create new ones.
     * @param appointmentId - updatable appointment id
     * @param weekdays - array of string digits. Each one represents day of week.
     * @param time - time array as a string. Format - hh:mm
     * @param period - appointment's number of weeks
     * @param dose - appointment's dose. If type is a procedure, automatically sets to zero.
     * @author Dmitriy Zhiburtovich
     */
    void updateAppointment(long appointmentId, String[] weekdays,
                                  String[] time, String period, String dose);

    /**
     * Cancel appointments and invoke delete events method, that delete all planned events.
     * @param appointmentId - id of appointment that being canceled
     * @author Dmitriy Zhiburtovich
     */
    void cancelAppointment(long appointmentId);


    /**
     * Find all active appointments that belong to patient
     * @param patientDTO - patient whose appointments returns method
     * @return list of appointments mapped to dto
     * @author Dmitriy Zhiburtovich
     */
    List<AppointmentDTO> findAllByPatient(PatientDTO patientDTO);


    /**
     *
     * @param id - appointment's id
     * @return dto of appointment
     * @author Dmitriy Zhiburtovich
     */
    AppointmentDTO findAppointmentById(long id);

    /**
     * Deletes appointment by id
     * @param id - id of appointment
     * @author Dmitriy Zhiburtovich
     */
    void deleteAppointment(long id);

    /**
     * Set appointment's boolean variable 'isCompleted' to true
     * @param id - appointment id
     * @author Dmitriy Zhiburtovich
     */
    void completeAppointment(long id);

    /**
     * Checks if there is no more planned events and complete appointment
     * @param appointments - list of appointment dtos that belong to patient
     * @author Dmitriy Zhiburtovich
     */
    void checkAppointmentsStatus(List<AppointmentDTO> appointments);

    /**
     * Set boolean active to false. After that appointment is no longer visible to the user
     * @param id - appointment id
     */
    void hideAppointment(long id);

}
