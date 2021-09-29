package com.example.rehab.models;

import com.example.rehab.models.enums.TypeOfAppointment;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@RequiredArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Patient patient;

    private TypeOfAppointment typeOfAppointment;

    private String timePattern;

    private String period;

    private String dose;

}
