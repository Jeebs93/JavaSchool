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
                                  String period, String dose, TypeOfAppointment typeOfAppointment) {
        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> timeList = Arrays.asList(time);
        List<String> resultTime = timeList.stream().filter(i -> !i.equals("")).collect(Collectors.toList());
        AppointmentDTO appointmentDTO = new AppointmentDTO(id, procedure, resultWeekDays,
                resultTime, Integer.parseInt(period), dose, typeOfAppointment);
        Appointment appointment = mapper.convertAppointmentToEntity(appointmentDTO);
        appointmentRepository.save(appointment);
        long appointmentId = appointment.getId();
        eventService.createEvents(appointmentDTO, appointmentId,0);

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
