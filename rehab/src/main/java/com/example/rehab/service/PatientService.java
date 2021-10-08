package com.example.rehab.service;

import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientMapper patientMapper;

    private final PatientRepository patientRepository;

    public List<PatientDTO> findAll() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(patientMapper::convertToDTO)
                .collect(Collectors.toList());
    }


}
