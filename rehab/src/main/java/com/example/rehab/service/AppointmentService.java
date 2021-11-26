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

    void cancelAppointment(long appointmentId);


    List<AppointmentDTO> findAllByPatient(PatientDTO patientDTO);


    AppointmentDTO findAppointmentById(long id);

    void deleteAppointment(long id);

    void completeAppointment(long id);

    void checkAppointmentsStatus(List<AppointmentDTO> appointments);

    void hideAppointment(long id);

}
