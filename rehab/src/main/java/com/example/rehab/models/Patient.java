package com.example.rehab.models;

import com.example.rehab.models.enums.PatientStatus;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private Long insuranceNumber;

    private String name;

    private PatientStatus patientStatus;

    private String doctor;

    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointmentList;

    @OneToMany
    private List<Event> eventList;

    public Patient(String name, Long insuranceNumber, String doctor, PatientStatus patientStatus) {
        this.name = name;
        this.insuranceNumber = insuranceNumber;
        this.doctor = doctor;
        this.patientStatus = patientStatus;
    }

}
