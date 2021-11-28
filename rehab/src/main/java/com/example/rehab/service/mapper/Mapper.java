package com.example.rehab.service.mapper;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.Patient;
import com.example.rehab.models.User;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.hibernate.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


import java.time.format.DateTimeFormatter;


@Component
@RequiredArgsConstructor
public class Mapper {



    private final ModelMapper modelMapper;




    public PatientDTO convertPatientToDTO(Patient patient) {
        return modelMapper.map(patient, PatientDTO.class);
    }

    public Patient convertPatientToEntity(PatientDTO patientDTO) {return modelMapper.map(patientDTO, Patient.class);}

    public UserDTO convertUserToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertUserToEntity(UserDTO userDTO) {return modelMapper.map(userDTO, User.class);}

    public AppointmentDTO convertAppointmentToDTO(Appointment appointment) {
        return modelMapper.map(appointment, AppointmentDTO.class);
    }

    public Appointment convertAppointmentToEntity(AppointmentDTO appointmentDTO) throws MappingException {
        return modelMapper.map(appointmentDTO, Appointment.class);
    }

    public EventDTO convertEventToDTO(Event event) {
        EventDTO eventDTO = modelMapper.map(event, EventDTO.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        eventDTO.setAppointmentValue(event.getAppointment().getValue());
        eventDTO.setDateString(event.getDate().format(formatter));
        eventDTO.setPatientName(event.getPatient().getName());
        return eventDTO;
    }

    public Event convertEventToEntity(EventDTO eventDTO) {return modelMapper.map(eventDTO, Event.class);}



}
