package com.example.rehab.repo;

import com.example.rehab.models.Patient;
import org.springframework.data.repository.CrudRepository;

public interface PatientRepository extends CrudRepository<Patient, Long> {
}
