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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.rehab.models.enums.TypeOfAppointment.PROCEDURE;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final Mapper mapper;

    private final AppointmentRepository appointmentRepository;

    private final EventService eventService;

    private final EventRepository eventRepository;

    public void createAppointment(long id, String procedure, String[] weekdays, String[] time,
                                  String period, String dose, TypeOfAppointment typeOfAppointment)  {
        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> timeList = Arrays.asList(time);
        List<String> resultTime = timeList.stream().filter(i -> !i.equals("")).collect(Collectors.toList());
        String timePattern = getTimePatternView(weekdays,resultTime,period);
        AppointmentDTO appointmentDTO = new AppointmentDTO(id, procedure, resultWeekDays,
                resultTime, Integer.parseInt(period), dose, typeOfAppointment,timePattern);
        Appointment appointment = mapper.convertAppointmentToEntity(appointmentDTO);
        appointmentRepository.save(appointment);
        long appointmentId = appointment.getId();
        eventService.createEvents(appointmentDTO, appointmentId,0);

    }

    public static String getTimePatternView(String[] weekDays, List<String> time, String period) {

        List<String> listWeekdays = new ArrayList<>();

        for (int i = 0; i < weekDays.length; i++) {
            switch (weekDays[i]) {
                case "1":listWeekdays.add("Monday");
                    break;
                case "2":listWeekdays.add("Tuesday");
                    break;
                case "3":listWeekdays.add("Wednesday");
                    break;
                case "4":listWeekdays.add("Thursday");
                    break;
                case "5":listWeekdays.add("Friday");
                    break;
                case "6":listWeekdays.add("Saturday");
                    break;
                case "7":listWeekdays.add("Sunday");
                    break;
            }
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
        String timePattern = resultWeekDays.size() == 1 ?
                "One day in a week at " + resultTime.toString() + " for " + period + " weeks." :
                resultWeekDays.size() + " days in a week at " + time.toString() + " for " + period + " weeks.";
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


    }

    public void cancelAppointment(long appointmentId) {
        Appointment appointment = appointmentRepository.getAppointmentById(appointmentId);
        appointment.setCancelled(true);
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
