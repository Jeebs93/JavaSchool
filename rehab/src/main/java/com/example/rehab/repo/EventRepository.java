package com.example.rehab.repo;

import com.example.rehab.models.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Long> {


}
