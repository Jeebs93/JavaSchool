package com.example.rehab.service;

import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.EventDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EventService {

    Page<EventDTO> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

    Page<EventDTO> findByPatientPaginated(int pageNo, int pageSize, int patientID);

    List<EventDTO> findAll();

    List<EventDTO> findByPatient(long id);

    boolean isToday(EventDTO eventDTO);

    boolean isRecent(EventDTO eventDTO);

    List<EventDTO> findAllRecent();

    List<EventDTO> findAllToday();

    void deleteEvents(long appointmentId);

    void completeEvent(long eventId);

    void cancelEvent(long eventId, String message);

    void createEvents(AppointmentDTO appointmentDTO, long appointmentId, int pastEvents);

    void hideEvent(long eventId);
}
