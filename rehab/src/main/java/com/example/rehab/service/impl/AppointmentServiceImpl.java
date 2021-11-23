package com.example.rehab.service.impl;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.DispatcherService;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.rehab.models.enums.EventStatus.PLANNED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final Mapper mapper;
    private final AppointmentRepository appointmentRepository;
    private final EventServiceImpl eventService;
    private final EventRepository eventRepository;
    private final DispatcherService dispatcherService;

    public long createAppointment(long id, String procedure, String[] weekdays, String[] time,
                                  String period, String dose, TypeOfAppointment typeOfAppointment) throws MappingException {
        try {
            Integer.parseInt(dose);
        } catch (NumberFormatException e) {
            dose = "0";
            log.warn("User did not specify a dose. It has been set to zero.");
        }
        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> timeList = Arrays.asList(time);
        List<String> resultTime = timeList.stream().filter(i -> !i.equals("")).collect(Collectors.toList());
        String timePattern = getTimePatternView(weekdays,resultTime,period);
        AppointmentDTO appointmentDTO = new AppointmentDTO(id, procedure, resultWeekDays,
                resultTime, Integer.parseInt(period), dose, typeOfAppointment,timePattern);
        Appointment appointment = mapper.convertAppointmentToEntity(appointmentDTO);
        appointmentRepository.save(appointment);
        log.info("Appointment has been created");
        long appointmentId = appointment.getId();
        eventService.createEvents(appointmentDTO, appointmentId,0);
        dispatcherService.sendMessage(dispatcherService.getMessage());
        return appointmentId;
    }

    public static String getTimePatternView(String[] weekDays, List<String> time, String period) {

        List<String> listWeekdays = new ArrayList<>();

        Map<String,String> week = new HashMap<>();
            week.put("1","Monday");
            week.put("2","Tuesday");
            week.put("3","Wednesday");
            week.put("4","Thursday");
            week.put("5","Friday");
            week.put("6","Saturday");
            week.put("7","Sunday");

        for (String weekDay : weekDays) {
            listWeekdays.add(week.get(weekDay));
        }

        String resultWeekdays = listWeekdays.toString().substring(1,listWeekdays.toString().length()-1);
        String weeks = Integer.parseInt(period) > 1 ? " weeks." : " week.";
        return "On " + resultWeekdays + " at " +
                  time.toString().substring(1,time.toString().length()-1) + " for " + period + weeks;
    }

    public void updateAppointment(long appointmentId, String[] weekdays,
                                  String[] time, String period, String dose) {

        Appointment appointment = appointmentRepository.getAppointmentById(appointmentId);
        AppointmentDTO appointmentDTO = mapper.convertAppointmentToDTO(appointment);
        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> timeList = Arrays.asList(time);
        List<String> resultTime = timeList.stream().filter(i -> !i.equals("")).collect(Collectors.toList());
        String timePattern = getTimePatternView(weekdays,resultTime,period);
        appointmentDTO.setWeekDays(resultWeekDays);
        appointmentDTO.setTime(resultTime);
        appointmentDTO.setTimePattern(timePattern);
        appointmentDTO.setDose(dose);
        appointmentDTO.setPeriod(Integer.parseInt(period));


        int pastEvents = 0;

        appointment = mapper.convertAppointmentToEntity(appointmentDTO);
        appointmentRepository.save(appointment);
        List<Event> events = eventRepository.findAllByAppointment(appointment);

        for (Event event:events) {
            if (!event.getEventStatus().equals(EventStatus.PLANNED)) {
                pastEvents++;
            } else {
                eventRepository.delete(event);
            }

        }
        eventService.createEvents(appointmentDTO, appointmentId, pastEvents);
        dispatcherService.sendMessage(dispatcherService.getMessage());
        log.info(String.format("Appointment with id %s has been updated", appointmentId));
    }

    public void cancelAppointment(long appointmentId) {
        Appointment appointment = appointmentRepository.getAppointmentById(appointmentId);
        appointment.setCanceled(true);
        appointment.setActive(false);
        log.info(String.format("Appointment with id %d has been canceled",appointmentId));
        appointmentRepository.save(appointment);
        eventService.deleteEvents(appointmentId);
        dispatcherService.sendMessage(dispatcherService.getMessage());
    }

    public List<AppointmentDTO> findAllByPatient(PatientDTO patientDTO) {
        Patient patient = mapper.convertPatientToEntity(patientDTO);
        List<Appointment> appointments = appointmentRepository.findAllByPatientAndIsActiveTrue(patient);

        return appointments.stream()
                .map(mapper::convertAppointmentToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO findAppointmentById(long id) {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        return mapper.convertAppointmentToDTO(appointment);
    }

    public void deleteAppointment(long id) {
        appointmentRepository.deleteById(id);
        log.info("Appointment has been deleted");
    }

    public void hideAppointment(long id) {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        appointment.setActive(false);
        eventService.hideEvents(id);
        appointmentRepository.save(appointment);
    }

    public void checkAppointmentsStatus(List<AppointmentDTO> appointments) {

        List<Appointment> appointmentList = appointments.stream()
                .map(mapper::convertAppointmentToEntity)
                .collect(Collectors.toList());
        for (Appointment appointment:appointmentList) {
            boolean unfinishedEvents = false;
            List<Event> eventList = eventRepository.findAllByAppointment(appointment);
            for (Event ev : eventList) {
                if (ev.getEventStatus().equals(PLANNED)) {
                    unfinishedEvents = true;
                    break;
                }

            }

            if (!unfinishedEvents && !appointment.isCompleted()) completeAppointment(appointment.getId());
        }
    }

    public void completeAppointment(long id) {
            Appointment appointment = appointmentRepository.getAppointmentById(id);
            appointment.setCompleted(true);
            appointmentRepository.save(appointment);
            log.info("Appointment has been completed");
    }

}
