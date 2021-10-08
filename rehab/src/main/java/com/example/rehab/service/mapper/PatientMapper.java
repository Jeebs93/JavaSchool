package com.example.rehab.service.mapper;

import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.PatientDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
public class PatientMapper {

    private final ModelMapper modelMapper;

    public PatientMapper() {
        this.modelMapper = new ModelMapper();
    }

    public PatientDTO convertToDTO(Patient patient) {
        return modelMapper.map(patient, PatientDTO.class);
    }

}
