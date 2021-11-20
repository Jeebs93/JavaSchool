package com.example.rehab.service;

import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.TypeOfAppointment;
import org.hibernate.MappingException;

import java.util.List;

public interface AppointmentService {


    long createAppointment(long id, String procedure, String[] weekdays, String[] time,
                                  String period, String dose, TypeOfAppointment typeOfAppointment);

    void updateAppointment(long appointmentId, String[] weekdays,
                                  String[] time, String period, String dose);

    void cancelAppointment(long appointmentId);


    List<AppointmentDTO> findAllByPatient(PatientDTO patientDTO);


    AppointmentDTO findAppointmentById(long id);

    void deleteAppointment(long id);
}
