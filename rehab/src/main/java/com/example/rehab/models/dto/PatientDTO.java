package com.example.rehab.models.dto;

import com.example.rehab.models.enums.PatientStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientDTO {

    private Long id;

    private Long insuranceNumber;

    private String name;

    private PatientStatus patientStatus;

    private String doctor;

    public PatientDTO(String name, Long insurance_number, String doctor, PatientStatus patientStatus) {
        this.name = name;
        this.insuranceNumber = insurance_number;
        this.doctor = doctor;
        this.patientStatus = patientStatus;
    }
}
