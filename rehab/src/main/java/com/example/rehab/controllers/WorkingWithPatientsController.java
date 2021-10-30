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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public String workingWithPatientsAdd(Model model) {
        model.addAttribute("patient",new PatientDTO());
        return "add-patient";
    }

    @PostMapping("/working-with-patients/add")
    public String workingWithPatientPostAdd(@Valid @ModelAttribute(name="patient") PatientDTO patient, BindingResult result,
                                            Model model) {
        if (result.hasErrors()) {
            return "add-patient";
        }
        patientService.createPatient(patient);
        return "redirect:/working-with-patients";
    }

    @GetMapping("/working-with-patients/{id}")
    public String patientDetails(@PathVariable(value = "id") long id, Model model) {
        PatientDTO result = patientService.getPatientDTObyID(id);
        List<AppointmentDTO> appointments = appointmentService.findAllByPatient(result);
        model.addAttribute("patient", result);
        model.addAttribute("patientId", id);
        model.addAttribute("appointment", appointments);
       return "patient-details";
    }

    @GetMapping("/working-with-patients/{id}/discharge")
    public String dischargePatient(@PathVariable(value = "id") long id) {
        patientService.dischargePatient(id);
        return "redirect:/working-with-patients";
    }

    @GetMapping("/working-with-patients/discharged")
    public String dischargedPatients(Model model) {
        List<PatientDTO> patients = patientService.findAll();
        model.addAttribute("patients",patients);
        return "discharged-patients";
    }




}
