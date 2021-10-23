package com.example.rehab.models.dto;

import com.example.rehab.models.enums.PatientStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
public class PatientDTO {

    private Long id;



    @NotEmpty(message = "Please, enter name of patient")
    private String name;

    @NotNull(message = "Please, enter insurance number")
    private Long insuranceNumber;

    private PatientStatus patientStatus;

    @NotEmpty(message = "Please, enter name of doctor")
    private String doctor;

    public PatientDTO(String name, Long insurance_number, String doctor, PatientStatus patientStatus) {
        this.name = name;
        this.insuranceNumber = insurance_number;
        this.doctor = doctor;
        this.patientStatus = patientStatus;
    }
}
