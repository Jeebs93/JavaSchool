package com.example.rehab.repo;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PatientRepository extends CrudRepository<Patient, Long>, PagingAndSortingRepository<Patient, Long> {

    List<Patient> findAll();

    Patient getPatientById(long id);

    long getIdByName(String name);

    Patient getPatientByName(String name);

    List<Patient> getPatientsByName(String name);
  //  Patient getPatientByAppointment(Appointment appointment);


}
