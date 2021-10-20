package com.example.rehab.service;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Event;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void createAppointment(long id, String procedure, String[] weekdays, String[] time,
                                  String period, String dose, TypeOfAppointment typeOfAppointment) {
        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> timeList = Arrays.asList(time);
        List<String> resultTime = timeList.stream().filter(i -> !i.equals("")).collect(Collectors.toList());
        AppointmentDTO appointmentDTO = new AppointmentDTO(id, procedure, resultWeekDays,
                resultTime, period, dose, typeOfAppointment);
        Appointment appointment = mapper.convertAppointmentToEntity(appointmentDTO);
        appointmentRepository.save(appointment);
        long appointmentId = appointment.getId();
        eventService.createEvents(appointmentDTO, appointmentId);

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

}
