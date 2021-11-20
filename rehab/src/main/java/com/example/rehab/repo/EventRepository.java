package com.example.rehab.repo;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {

    List<Event> findAll();

    List<Event> findAllByActiveTrue();

    Page<Event> findAllByActiveTrue(Pageable pageable);

    Page<Event> findAllByPatient(Pageable pageable,Patient patient);

    Page<Event> findAllByPatientAndActiveTrue(Pageable pageable,Patient patient);

    Event findEventById(long id);

    List<Event> findAllByAppointment(Appointment appointment);

    void delete(Event event);

}
