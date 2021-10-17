package com.example.rehab.repo;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

    List<Appointment> findAll();

    List<Appointment> findAllByPatient(Patient patient);

    Appointment getAppointmentById(long id);

}
