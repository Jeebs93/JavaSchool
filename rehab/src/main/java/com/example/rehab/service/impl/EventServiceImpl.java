package com.example.rehab.service.impl;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.DispatcherService;
import com.example.rehab.service.EventService;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.rehab.models.enums.EventStatus.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final Mapper mapper;
    private final EventRepository eventRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DispatcherService dispatcherService;



    public Page<EventDTO> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1,pageSize, sort);
        List<Event> events = eventRepository.findAll();

        LocalDateTime date = LocalDateTime.now();

        for (Event event:events) {
            if (event.getEventStatus().equals(PLANNED) &&
                    (event.getDate().getDayOfYear()<date.getDayOfYear() && (event.getDate().getYear())==date.getYear())) {
                event.setEventStatus(CANCELED);
                event.setMessage("Event hasn't been marked as completed.");
                eventRepository.save(event);
                Appointment appointment = event.getAppointment();
                appointment.setCanceledEvents(appointment.getCanceledEvents()+1);
                appointmentRepository.save(appointment);
            }
        }

        return this.eventRepository.findAllByActiveTrue(pageable).map(mapper::convertEventToDTO);

    }

    public Page<EventDTO> findByPatientPaginated(int pageNo, int pageSize, int patientID) {
        Sort sort = Sort.by("date").ascending();
        Pageable pageable = PageRequest.of(pageNo-1,pageSize, sort);
        return this.eventRepository
                .findAllByPatientAndActiveTrue(pageable,patientRepository.getPatientById(patientID))
                .map(mapper::convertEventToDTO);

    }



    public List<EventDTO> findAll() {
        return eventRepository.findAllByActiveTrue().stream().map(mapper::convertEventToDTO).collect(Collectors.toList());
    }


    public List<EventDTO> findByPatient(long id) {
        List<EventDTO> events = findAll();
        List<EventDTO> result = new ArrayList<>();
        if (!events.isEmpty()) {
            for (EventDTO event : events) {
                if (event.getPatient().getId().equals(id)) {
                    result.add(event);
                }
            }
            Collections.sort(result, Comparator.comparing(EventDTO::getDateString));
        }
        return result;
    }


    public EventDTO findById(long id) {
        return mapper.convertEventToDTO(eventRepository.findEventById(id));
    }


    public boolean isToday(EventDTO eventDTO) {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime eventDate = eventDTO.getDate();
        return date.getDayOfYear()==eventDate.getDayOfYear() && date.getYear()==eventDate.getYear();
    }

    public boolean isRecent(EventDTO eventDTO) {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime eventDate = eventDTO.getDate();
        return isToday(eventDTO) &&
                (((date.getHour() == eventDate.getHour())
                        && (date.getMinute() < eventDate.getMinute()))
                        || (date.getHour()+1 == eventDate.getHour()));
    }


    public List<EventDTO> findAllRecent() {
        List<EventDTO> events = findAll();
        List<EventDTO> result = new ArrayList<>();
        for (EventDTO event:events) {
            if (isRecent(event)) {
                result.add(event);
            }
        }
        Collections.sort(result, Comparator.comparing(EventDTO::getDateString));
        return result;
    }

    public List<EventDTO> findAllToday(){
        List<EventDTO> events = findAll();
        List<EventDTO> result = new ArrayList<>();
        for (EventDTO event:events) {
            if (isToday(event)) {
                result.add(event);
            }
        }

        Collections.sort(result, Comparator.comparing(EventDTO::getDateString));

        return result;
    }

    public void deleteEvents(long appointmentId) {
        Appointment appointment = appointmentRepository.getAppointmentById(appointmentId);
        List<Event> events = eventRepository.findAllByAppointment(appointment);
        for (Event event:events) {
            if (event.getEventStatus().equals(PLANNED)) {
                eventRepository.delete(event);
            }
        }
        log.info(String.format("Appointment's(id = %s) events have been deleted",appointmentId));
    }

    public void hideEvents(long appointmentId) {
        Appointment appointment = appointmentRepository.getAppointmentById(appointmentId);
        List<Event> events = eventRepository.findAllByAppointment(appointment);
        events.forEach(event -> hideEvent(event.getId()));
        log.info(String.format("Appointment's(id = %s) events have been hidden",appointmentId));
    }

    public void completeEvent(long eventId) {
        Event event = eventRepository.findEventById(eventId);
        if (isToday(mapper.convertEventToDTO(event))) {
            event.setEventStatus(COMPLETED);
            eventRepository.save(event);
            log.info(String.format("Event with id %s has been hidden",eventId));
            dispatcherService.sendMessage(dispatcherService.getMessage());
        } else throw new IllegalStateException();

    }

    public void cancelEvent(long eventId, String message) {
        Event event = eventRepository.findEventById(eventId);
        event.setEventStatus(CANCELED);
        event.setMessage(message);
        Appointment appointment = event.getAppointment();
        appointment.setCanceledEvents(appointment.getCanceledEvents()+1);
        appointmentRepository.save(appointment);
        eventRepository.save(event);
        log.info(String.format("Event with id %s has been canceled",eventId));
        dispatcherService.sendMessage(dispatcherService.getMessage());
    }

    public void hideEvent(long eventId) {
        Event event = eventRepository.findEventById(eventId);
        event.setActive(false);
        eventRepository.save(event);
        log.info(String.format("Event with id %s has been hidden",eventId));
    }

    public void createEvents(AppointmentDTO appointmentDTO, long appointmentId, int pastEvents) {
        List<String> timeList = appointmentDTO.getTime();
        int periodInt = appointmentDTO.getPeriod();
        int numberOfEvents = appointmentDTO.getWeekDays().size() * timeList.size()
                * periodInt;
        List<Event> events = new ArrayList<>(numberOfEvents);
        int timeCounter = 0;
        int dateCounter = 0;
        List<LocalDateTime> dateList = generateDateList(appointmentDTO.getWeekDays(), periodInt);

        for (int i = 0; i < numberOfEvents-pastEvents; i++) {
            Event event = Event.builder()
                    .patient(patientRepository.getPatientById(appointmentDTO.getPatientId()))
                    .date(parseDate((dateList.get(dateCounter)),timeList.get(timeCounter)))
                    .eventStatus(PLANNED)
                    .typeOfAppointment(appointmentDTO.getTypeOfAppointment())
                    .appointment(appointmentRepository.getAppointmentById(appointmentId))
                    .active(true)
                    .message("")
                    .build();

            events.add(event);
            timeCounter++;
            if (timeCounter >= appointmentDTO.getTime().size()) {
                timeCounter = 0;
                dateCounter++;
            }
            if (dateCounter >= dateList.size()) {
                dateCounter = 0;
            }
        }
        eventRepository.saveAll(events);
        dispatcherService.sendMessage(dispatcherService.getMessage());
        log.info(String.format("Events for appointment with id %d have been created",appointmentId));
    }

    public List<EventDTO> findAllByAppointmentId(long id) {
        return eventRepository.findAllByAppointmentId(id)
                .stream()
                .map(mapper::convertEventToDTO)
                .collect(Collectors.toList());
    }

    private static List<LocalDateTime> generateDateList(List<String> weekDays, int period) {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        List<LocalDateTime> resultList = new ArrayList<>();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfWeekIntValue = dayOfWeek.getValue();
        boolean flag = false;

        for (int i = 0; i < weekDays.size()*period; i++) {
            flag = false;
            while (!flag) {
                if (weekDays.contains(Integer.toString(dayOfWeekIntValue))) {
                    resultList.add(date);
                    date = date.plusDays(1);
                    dayOfWeek = date.getDayOfWeek();
                    dayOfWeekIntValue = dayOfWeek.getValue();
                    flag = true;
                } else {
                    date = date.plusDays(1);
                    dayOfWeek = date.getDayOfWeek();
                    dayOfWeekIntValue = dayOfWeek.getValue();
                }
            }
        }
        return resultList;
    }

    private static LocalDateTime parseDate(LocalDateTime date, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String strDate = date.format(formatter);
        strDate += " " + time;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(strDate, formatter);
    }



}
