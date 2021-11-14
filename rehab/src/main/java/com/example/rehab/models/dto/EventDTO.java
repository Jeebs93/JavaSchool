package com.example.rehab.models.dto;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventDTO {

    private Long id;

    private Patient patient;

    private Appointment appointment;

    private LocalDateTime date;

    @Expose
    private String patientName;

    @Expose
    private String appointmentValue;

    @Expose
    private String dateString;

    private EventStatus eventStatus;

    private TypeOfAppointment typeOfAppointment;

    private Long appointmentId;

    private String message;

}
