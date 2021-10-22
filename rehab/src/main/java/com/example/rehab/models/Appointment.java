package com.example.rehab.models;

import com.example.rehab.models.enums.TypeOfAppointment;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="patient_id", nullable = false)
    private Patient patient;

    @OneToMany(mappedBy = "appointment")
    private List<Event> eventList;

    private TypeOfAppointment typeOfAppointment;

    private String value;

    private String timePattern;

    private Integer period;

    private Integer dose;

    boolean isCancelled;



}
