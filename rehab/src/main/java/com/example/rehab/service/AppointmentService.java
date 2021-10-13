package com.example.rehab.service;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final Mapper mapper;

    private final AppointmentRepository appointmentRepository;

    public void createAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = mapper.convertAppointmentToEntity(appointmentDTO);
        appointmentRepository.save(appointment);
    }

    public List<AppointmentDTO> findAllByPatient(Patient patient) {
        List<Appointment> appointments = appointmentRepository.findAllByPatient(patient);

        return appointments.stream()
                .map(mapper::convertAppointmentToDTO)
                .collect(Collectors.toList());
    }

}
