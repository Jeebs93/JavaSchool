package com.example.rehab.models;

import com.example.rehab.models.enums.PatientStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long insuranceNumber;

    private String name;

    private PatientStatus patientStatus;

    private String doctor;

    @OneToMany
    private List<Appointment> appointmentList;

    @OneToMany
    private List<Event> eventList;

}
