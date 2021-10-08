package com.example.rehab.models.dto;

import com.example.rehab.models.enums.PatientStatus;
import lombok.Data;

@Data
public class PatientDTO {

    private Long id;

    private Long insuranceNumber;

    private String name;

    private PatientStatus patientStatus;

    private String doctor;

}
