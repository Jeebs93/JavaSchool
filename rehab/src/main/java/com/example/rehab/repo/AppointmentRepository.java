package com.example.rehab.repo;

import com.example.rehab.models.Appointment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppointmentRepository extends CrudRepository<Appointment, Long> {



}
