package com.example.rehab.controllers;

import com.example.rehab.models.Patient;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WorkingWithPatientsController {

    private PatientRepository patientRepository;

    @Autowired
    public WorkingWithPatientsController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/working-with-patients")
    public String workingWithPatients(Model model) {
        Iterable<Patient> patients = patientRepository.findAll();
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

}
