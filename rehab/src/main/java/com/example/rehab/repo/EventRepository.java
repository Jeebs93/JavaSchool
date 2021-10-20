package com.example.rehab.repo;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {

    List<Event> findAll();

    Event findEventById(long id);

    List<Event> findAllByAppointment(Appointment appointment);

    void delete(Event event);

}
