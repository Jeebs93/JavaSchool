package com.example.rehab.models;

import com.example.rehab.models.enums.PatientStatus;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(unique = true)
    private Long insuranceNumber;

    private String name;

    private String diagnosis;

    private PatientStatus patientStatus;

    private String doctor;

    @OneToMany(mappedBy = "patient", cascade=CascadeType.ALL,orphanRemoval = true)
    private List<Appointment> appointmentList;

    @OneToMany(mappedBy = "patient", cascade=CascadeType.ALL,orphanRemoval = true)
    private List<Event> eventList;

    public Patient(String name, Long insuranceNumber, String doctor, PatientStatus patientStatus) {
        this.name = name;
        this.insuranceNumber = insuranceNumber;
        this.doctor = doctor;
        this.patientStatus = patientStatus;
    }

}
