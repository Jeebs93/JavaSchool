package com.example.rehab.controllers;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.PatientService;
import lombok.RequiredArgsConstructor;
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



    private AppointmentRepository appointmentRepository;

    @Autowired
    public WorkingWithPatientsController(PatientRepository patientRepository, PatientService patientService) {
        this.patientRepository = patientRepository;
        this.patientService = patientService;
    }

    @GetMapping("/working-with-patients")
    public String workingWithPatients(Model model) {
       List<PatientDTO> patients = patientService.findAll();
       model.addAttribute("patients", patients);
       return "working-with-patients";
    }

    @GetMapping("/working-with-patients/add")
    public String workingWithPatientsAdd(Model model) {
        return "add-patient";
    }

    @PostMapping("/working-with-patients/add")
    public String workingWithPatientPostAdd(@RequestParam String name,
                                            @RequestParam Long insurance_number,
                                            @RequestParam String doctor,
                                            Model model) {
        Patient patient = new Patient(name, insurance_number, doctor, PatientStatus.ON_TREATMENT);
        patientRepository.save(patient);
        return "redirect:/working-with-patients";
    }

    @GetMapping("/working-with-patients/{id}")
    public String patientDetails(@PathVariable(value = "id") long id, Model model) {
       Optional<Patient> patient = patientRepository.findById(id);
       Patient result = patient.get();
       model.addAttribute("patient", result);
       return "patient-details";
    }

    @GetMapping("/working-with-patients/{id}/add-appointment")
    public String addAppointment(@PathVariable(value = "id") long id, Model model) {
        Optional<Patient> patient = patientRepository.findById(id);
        Patient result = patient.get();
        model.addAttribute("patient", result);
        return "add-appointment";
    }

}
