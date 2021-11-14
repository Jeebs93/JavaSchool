package com.example.rehab.controllers;

import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.PatientService;
import com.example.rehab.service.impl.AppointmentServiceImpl;
import com.example.rehab.service.impl.PatientServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class PatientsController {


    private PatientService patientService;

    private PatientRepository patientRepository;

    private AppointmentService appointmentService;



    private AppointmentRepository appointmentRepository;

    private Mapper mapper;

    @Autowired
    public PatientsController(PatientRepository patientRepository, PatientService patientService,
                              AppointmentService appointmentService,
                              Mapper mapper) {
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.mapper = mapper;
    }

    @GetMapping("/patients")
    public String workingWithPatients(Model model) {
       List<PatientDTO> patients = patientService.findAll();
       model.addAttribute("patients", patients);
       return "patients";
    }

    @GetMapping("/patients/new")
    public String workingWithPatientsAdd(Model model) {
        model.addAttribute("patient",new PatientDTO());
        return "add-patient";
    }

    @PostMapping("/patients/new")
    public String workingWithPatientPostAdd(@Valid @ModelAttribute(name="patient") PatientDTO patient, BindingResult result,
                                            Model model) {
        if (result.hasErrors()) {
            return "add-patient";
        }
        patientService.createPatient(patient);
        return "redirect:/patients";
    }

    @GetMapping("/patients/{id}")
    public String patientDetails(@PathVariable(value = "id") long id, Model model) {
        PatientDTO result = patientService.getPatientByID(id);
        List<AppointmentDTO> appointments = appointmentService.findAllByPatient(result);
        model.addAttribute("patient", result);
        model.addAttribute("patientId", id);
        model.addAttribute("appointment", appointments);
       return "patient-details";
    }

    @GetMapping("/patients/{id}/discharge")
    public String dischargePatient(@PathVariable(value = "id") long id) {
        patientService.dischargePatient(id);
        return "redirect:/patients";
    }

    @GetMapping("/patients/discharged")
    public String dischargedPatients(Model model) {
        List<PatientDTO> patients = patientService.findAll();
        model.addAttribute("patients",patients);
        return "discharged-patients";
    }

    @GetMapping("/patients/{id}/return")
    public String returnPatient(@PathVariable(value = "id") long id) {
        patientService.returnPatient(id);
        return "redirect:/patients/discharged";
    }




}
