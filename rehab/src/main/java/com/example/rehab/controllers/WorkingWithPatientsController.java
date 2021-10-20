package com.example.rehab.controllers;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.PatientService;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class WorkingWithPatientsController {


    private PatientService patientService;

    private PatientRepository patientRepository;

    private AppointmentService appointmentService;



    private AppointmentRepository appointmentRepository;

    private Mapper mapper;

    @Autowired
    public WorkingWithPatientsController(PatientRepository patientRepository, PatientService patientService,
                                         AppointmentService appointmentService,
                                         Mapper mapper) {
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.mapper = mapper;
    }

    @GetMapping("/working-with-patients")
    public String workingWithPatients(Model model) {
       List<PatientDTO> patients = patientService.findAll();
       model.addAttribute("patients", patients);
       return "working-with-patients";
    }

    @GetMapping("/working-with-patients/add")
    public String workingWithPatientsAdd() {
        return "add-patient";
    }

    @PostMapping("/working-with-patients/add")
    public String workingWithPatientPostAdd(@RequestParam String name,
                                            @RequestParam Long insurance_number,
                                            @RequestParam String doctor,
                                            Model model) {
        PatientDTO patientDTO = new PatientDTO(name, insurance_number, doctor, PatientStatus.ON_TREATMENT);
        patientService.createPatient(patientDTO);
        return "redirect:/working-with-patients";
    }

    @GetMapping("/working-with-patients/{id}")
    public String patientDetails(@PathVariable(value = "id") long id, Model model) {
        PatientDTO result = patientService.getPatientDTObyID(id);
        List<AppointmentDTO> appointments = appointmentService.findAllByPatient(mapper.convertPatientToEntity(result));
        model.addAttribute("patient", result);
        model.addAttribute("patientId", id);
        model.addAttribute("appointment", appointments);
       return "patient-details";
    }



}
