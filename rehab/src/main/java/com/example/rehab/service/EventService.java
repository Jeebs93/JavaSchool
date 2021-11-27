package com.example.rehab.service;

import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.EventDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    /**
     * Find page of all event dtos
     * @param pageNo - number of page
     * @param pageSize - number of elements in one page
     * @param sortField - events can be sorted by date or by patient name
     * @param sortDirection - sort can be ascending or descending
     * @return page with event dtos
     * @author Dmitriy Zhiburtovich
     */
    Page<EventDTO> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

    /**
     * Find page of event dtos by patient id
     * @param pageNo - number of page
     * @param pageSize - number of elements in one page
     * @param patientID - id of patient
     * @return page with event dtos by patient id
     * @author Dmitriy Zhiburtovich
     */
    Page<EventDTO> findByPatientPaginated(int pageNo, int pageSize, int patientID);

    /**
     * Find all active events
     * @return list of event dtos
     * @author Dmitriy Zhiburtovich
     */
    List<EventDTO> findAll();

    /**
     * Depending on the date determines the event today or not
     * @author Dmitriy Zhiburtovich
     */
    boolean isToday(EventDTO eventDTO);

    /**
     * Depending on the datetime determines the event is in this or next hour
     * @author Dmitriy Zhiburtovich
     */
    boolean isRecent(EventDTO eventDTO);

    /**
     * Finds all events which should happen at this or next hour
     * @author Dmitriy Zhiburtovich
     */
    List<EventDTO> findAllRecent();

    /**
     * Finds all events which should will be today
     * @author Dmitriy Zhiburtovich
     */
    List<EventDTO> findAllToday();

    /**
     * Delete all planned events by appointment id
     * @author Dmitriy Zhiburtovich
     */
    void deleteEvents(long appointmentId);

    /**
     * Complete event, if its today - otherwise throws exception
     * @author Dmitriy Zhiburtovich
     */
    void completeEvent(long eventId);

    /**
     * Change event status to canceled and sets message with reason
     * @param message - message with explanation of reason why event canceled
     * @author Dmitriy Zhiburtovich
     */
    void cancelEvent(long eventId, String message);

    /**
     * Create events which attributes depend on appointment information. Date countdown starts from tomorrow.
     * @param appointmentDTO - dto of appointment model with all required information
     * @param pastEvents - not equals to zero in cases where appointment is being updated. Counts all appointment events,
     *                   that appointment status is not 'PLANNED'
     * @author Dmitriy Zhiburtovich
     */
    void createEvents(AppointmentDTO appointmentDTO, long appointmentId, int pastEvents);

    /**
     * Set event boolean variable 'isActive' to false. After that event is no longer visible to the user
     * @author Dmitriy Zhiburtovich
     */
    void hideEvent(long eventId);

    /**
     * Hide all appointment events. Invokes after hiding appointment.
     * @author Dmitriy Zhiburtovich
     */
    void hideEvents(long appointmentId);

    /**
     * Finds event by id and converts it to dto
     * @author Dmitriy Zhiburtovich
     */
    EventDTO findById(long id);

    /**
     * Finds all events with by patient
     * @param id - patient id
     * @author Dmitriy Zhiburtovich
     */
    List<EventDTO> findByPatient(long id);

    /**
     * Finds all events with by appointment
     * @param id - appointment id
     * @author Dmitriy Zhiburtovich
     */
    List<EventDTO> findAllByAppointmentId(long id);
}
