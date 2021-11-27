package com.example.rehab.models;

import com.example.rehab.models.enums.TypeOfAppointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ProceduresAndCures {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private TypeOfAppointment typeOfAppointment;

}
