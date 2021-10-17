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
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final Mapper mapper;

    private final EventRepository eventRepository;

    private final PatientRepository patientRepository;

    private final AppointmentRepository appointmentRepository;

    public List<EventDTO> findAll() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(mapper::convertEventToDTO)
                .collect(Collectors.toList());
    }

    public void createEvents(AppointmentDTO appointmentDTO, long appointmentId) {

        List<String> timeList = appointmentDTO.getTime();
        int periodInt = Integer.parseInt(appointmentDTO.getPeriod());
        int numberOfEvents = appointmentDTO.getWeekDays().size() * timeList.size()
                * periodInt;
        List<Event> events = new ArrayList<>(numberOfEvents);

        int timeCounter = 0;
        int dateCounter = 0;
        List<LocalDateTime> dateList = generateDateList(appointmentDTO.getWeekDays(), periodInt);

        for (int i = 0; i < numberOfEvents; i++) {
            Event event = new Event();

            event.setPatient(patientRepository.getPatientById(appointmentDTO.getPatientId()));
            event.setDate(parseDate((dateList.get(dateCounter)),timeList.get(timeCounter)));
            event.setEventStatus(EventStatus.PLANNED);
            event.setTypeOfAppointment(appointmentDTO.getTypeOfAppointment());
            event.setAppointment(appointmentRepository.getAppointmentById(appointmentId));
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
