package com.example.rehab.service;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.rehab.models.enums.TypeOfAppointment.PROCEDURE;
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final Mapper mapper;

    private final AppointmentRepository appointmentRepository;

    private final EventService eventService;

    private final EventRepository eventRepository;

    public void createAppointment(long id, String procedure, String[] weekdays, String[] time,
                                  String period, String dose, TypeOfAppointment typeOfAppointment) throws MappingException {
        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> timeList = Arrays.asList(time);
        List<String> resultTime = timeList.stream().filter(i -> !i.equals("")).collect(Collectors.toList());
        String timePattern = getTimePatternView(weekdays,resultTime,period);
        try {
           int doseInt = Integer.parseInt(dose);
        } catch (NumberFormatException e) {
            dose = "0";
            log.warn("Wrong input type for dose");
        }
        AppointmentDTO appointmentDTO = new AppointmentDTO(id, procedure, resultWeekDays,
                resultTime, Integer.parseInt(period), dose, typeOfAppointment,timePattern);
        Appointment appointment = mapper.convertAppointmentToEntity(appointmentDTO);
        appointmentRepository.save(appointment);
        log.info("Appointment has been created");
        long appointmentId = appointment.getId();
        eventService.createEvents(appointmentDTO, appointmentId,0);

    }

    public static String getTimePatternView(String[] weekDays, List<String> time, String period) {

        List<String> listWeekdays = new ArrayList<>();

        Map<String,String> week = new HashMap<>() {{
            put("1","Monday");
            put("2","Tuesday");
            put("3","Wednesday");
            put("4","Thursday");
            put("5","Friday");
            put("6","Saturday");
            put("7","Sunday");
        }};

        for (int i = 0; i < weekDays.length; i++) {
           listWeekdays.add(week.get(weekDays[i]));
        }

        String resultWeekdays = listWeekdays.toString().substring(1,listWeekdays.toString().length()-1);

        String weeks = Integer.parseInt(period) > 1 ? " weeks." : " week.";

          String timePattern = "On " + resultWeekdays + " at " +
                  time.toString().substring(1,time.toString().length()-1) + " for " + period + weeks;

          return timePattern;
    }

    public void updateAppointment(AppointmentDTO appointmentDTO, String[] weekdays,
                                  String[] time, String period, String dose) {

        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> timeList = Arrays.asList(time);
        List<String> resultTime = timeList.stream().filter(i -> !i.equals("")).collect(Collectors.toList());
        String timePattern = getTimePatternView(weekdays,resultTime,period);
        appointmentDTO.setWeekDays(resultWeekDays);
        appointmentDTO.setTime(resultTime);
        appointmentDTO.setTimePattern(timePattern);
        appointmentDTO.setDose(dose);
        appointmentDTO.setPeriod(Integer.parseInt(period));

        long appointmentId = appointmentDTO.getId();

        int pastEvents = 0;

        LocalDateTime date = LocalDateTime.now();
        Appointment appointment = mapper.convertAppointmentToEntity(appointmentDTO);
        appointmentRepository.save(appointment);
        List<Event> events = eventRepository.findAllByAppointment(appointment);

        for (Event event:events) {
            if (!event.getEventStatus().equals(EventStatus.PLANNED)) {
                pastEvents++;
            } else {
                eventRepository.delete(event);
            }

          /*  if (event.getDate().isAfter(date)) {
                eventRepository.delete(event);
            }*/
        }


        eventService.createEvents(appointmentDTO, appointmentId, pastEvents);
        log.info("Appointment has been updated");


    }

    public void cancelAppointment(long appointmentId) {
        Appointment appointment = appointmentRepository.getAppointmentById(appointmentId);
        appointment.setCancelled(true);
        log.info("Appointment has been canceled");
        appointmentRepository.save(appointment);
        eventService.deleteEvents(appointmentId);
    }

    public List<AppointmentDTO> findAllByPatient(Patient patient) {
        List<Appointment> appointments = appointmentRepository.findAllByPatient(patient);

        return appointments.stream()
                .map(mapper::convertAppointmentToDTO)
                .collect(Collectors.toList());
    }

    public Appointment findAppointmentById(long id) {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        return appointment;
    }

}
