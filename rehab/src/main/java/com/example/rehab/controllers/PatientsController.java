package com.example.rehab.controllers;

import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.PatientService;
import com.example.rehab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@Slf4j
@Controller
public class PatientsController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    private static final String PATIENTS_REDIRECT = "redirect:/patients";
    private static final String PATIENT = "patient";
    private static final String PATIENTS = "patients";

    @Autowired
    public PatientsController(PatientService patientService,
                              AppointmentService appointmentService,
                              UserService userService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping("/patients")
    public String workingWithPatients(Model model) {
        String username = userService.getUserName();
       List<PatientDTO> patients = patientService.findAll();
       model.addAttribute(PATIENTS, patients);
       model.addAttribute("username",username);
       return PATIENTS;
    }

    @GetMapping("/patients/my-patients")
    public String myPatients(Model model) {
        String username = userService.getUserName();
        List<PatientDTO> patients = patientService.getPatientsByDoctor(username);
        model.addAttribute(PATIENTS,patients);
        return "my-patients";
    }

    @GetMapping("/patients/new")
    public String workingWithPatientsAdd(Model model) {
        model.addAttribute(PATIENT,new PatientDTO());
        List<UserDTO> doctors = userService.findAllDoctors();
        model.addAttribute("doctors",doctors);
        return "add-patient";
    }

    @PostMapping("/patients/new")
    public String workingWithPatientPostAdd(@Valid @ModelAttribute(name="patient") PatientDTO patient, BindingResult result,
                                            Model model) {
        if (result.hasErrors()) {
            List<UserDTO> doctors = userService.findAllDoctors();
            model.addAttribute("doctors",doctors);
            return "add-patient";
        }
        patientService.createPatient(patient);
        return PATIENTS_REDIRECT;
    }

    @GetMapping("/patients/{id}")
    public String patientDetails(@PathVariable(value = "id") long id, Model model) {
        PatientDTO result = patientService.getPatientByID(id);
        appointmentService.checkAppointmentsStatus(appointmentService.findAllByPatient(result));
        List<AppointmentDTO> appointments = appointmentService.findAllByPatient(result);
        String doctor = userService.findByUserName(result.getDoctor()).getUsername();
        model.addAttribute("doctor", doctor);
        model.addAttribute(PATIENT, result);
        model.addAttribute("patientId", id);
        model.addAttribute("appointment", appointments);
       return "patient-details";
    }

    @GetMapping("/patients/discharge/{id}")
    public String dischargePatient(@PathVariable(value = "id") long id) {
        if (patientService.hasUnfinishedAppointments(id)){
            return "redirect:/patients/unfinished-appointments/" + id;
        }
        patientService.dischargePatient(id);
        return PATIENTS_REDIRECT;
    }

    @GetMapping("/patients/unfinished-appointments/{id}")
    public String unfinishedAppointments(@PathVariable(value = "id") long id, Model model) {
        List<AppointmentDTO> appointments = appointmentService.findAllByPatient(patientService.getPatientByID(id));
        PatientDTO patient = patientService.getPatientByID(id);
        model.addAttribute(PATIENT, patient);
        model.addAttribute("patientId",id);
        model.addAttribute("appointments", appointments);
        return "unfinished-appointments";
    }

    @PostMapping("/patients/unfinished-appointments/{id}")
    public String unfinishedAppointmentsPost(@PathVariable(value = "id") long id) {
        patientService.dischargePatient(id);
        return PATIENTS_REDIRECT;
    }

    @GetMapping("/patients/discharged")
    public String dischargedPatients(Model model) {
        List<PatientDTO> patients = patientService.findAll();
        model.addAttribute(PATIENTS,patients);
        return "discharged-patients";
    }

    @GetMapping("/patients/return/{id}")
    public String returnPatient(@PathVariable(value = "id") long id) {
        patientService.returnPatient(id);
        return "redirect:/patients/discharged";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleInsuranceNumberException(DataIntegrityViolationException e, Model model) {
        log.warn("Insurance number is not unique");
        String message = "Insurance number is not unique";
        model.addAttribute("message",message);
        model.addAttribute("path","/patients/new");
        return "error-page";
    }


}
