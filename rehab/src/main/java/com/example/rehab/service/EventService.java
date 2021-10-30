package com.example.rehab.service;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.rehab.models.enums.EventStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final Mapper mapper;

    private final EventRepository eventRepository;

    private final PatientRepository patientRepository;

    private final AppointmentRepository appointmentRepository;

    public Page<EventDTO> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1,pageSize, sort);



        return this.eventRepository.findAll(pageable).map(mapper::convertEventToDTO);

    }



    public List<EventDTO> findAll() {


        List<Event> events = eventRepository.findAll();

        List<EventDTO> result = events.stream().map(mapper::convertEventToDTO).collect(Collectors.toList());

        return result;
    }

    public List<EventDTO> findByPatient(long id) {
        List<EventDTO> events = findAll();
        List<EventDTO> result = new ArrayList<>();
        if (events.size() > 0) {
            for (EventDTO event : events) {
                if (event.getPatient().getId().equals(id)) {
                    result.add(event);
                }
            }
        }
        return result;
    }

    public boolean isToday(EventDTO eventDTO) {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime eventDate = eventDTO.getDate();
        return date.getDayOfYear()==eventDate.getDayOfYear();
    }

    public boolean isRecent(EventDTO eventDTO) {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime eventDate = eventDTO.getDate();
        return isToday(eventDTO) &&
                ((date.getHour() == eventDate.getHour()) || (date.getHour()+1 == eventDate.getHour()));
    }


    public List<EventDTO> findAllRecent() {
        List<EventDTO> events = findAll();
        List<EventDTO> result = new ArrayList<>();
        for (EventDTO event:events) {
            if (isRecent(event)) {
                result.add(event);
            }
        }
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
        log.info("Appointment events have been deleted");
    }

    public void completeEvent(long eventId) {
        Event event = eventRepository.findEventById(eventId);
        event.setEventStatus(COMPLETED);
        event.setMessage("");
        eventRepository.save(event);
    }

    public void cancelEvent(long eventId, String message) {
        Event event = eventRepository.findEventById(eventId);
        event.setEventStatus(CANCELED);
        event.setMessage(message);
        eventRepository.save(event);
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
            Event event = new Event();

            event.setPatient(patientRepository.getPatientById(appointmentDTO.getPatientId()));
            event.setDate(parseDate((dateList.get(dateCounter)),timeList.get(timeCounter)));
            event.setEventStatus(EventStatus.PLANNED);
            event.setTypeOfAppointment(appointmentDTO.getTypeOfAppointment());
            event.setAppointment(appointmentRepository.getAppointmentById(appointmentId));
            event.setMessage("");
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
        log.info("Events have been created");
    }

    private static List<LocalDateTime> generateDateList(List<String> weekDays, int period) {

        LocalDateTime date = LocalDateTime.now();
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
