package com.example.rehab.models.dto;

import com.example.rehab.models.enums.PatientStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.*;


@Data
@NoArgsConstructor
public class PatientDTO {

    private Long id;



    @NotEmpty(message = "Please, enter name of patient")
    private String name;

    @NotNull(message = "Please, enter insurance number")
    private Long insuranceNumber;

    @NotEmpty(message = "Please, enter diagnosis")
    public String diagnosis;

    private PatientStatus patientStatus;

    @NotEmpty(message = "Please, enter name of doctor")
    private String doctor;

    public PatientDTO(String name, Long insurance_number, String diagnosis,
                      String doctor, PatientStatus patientStatus) {
        this.name = name;
        this.insuranceNumber = insurance_number;
        this.diagnosis = diagnosis;
        this.doctor = doctor;
        this.patientStatus = patientStatus;
    }
}
