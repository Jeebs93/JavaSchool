package com.example.rehab.controllers;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.Patient;
import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.repo.ProceduresAndCuresRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.EventService;
import com.example.rehab.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.example.rehab.models.enums.TypeOfAppointment.PROCEDURE;
import static com.example.rehab.models.enums.TypeOfAppointment.CURE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {

    private PatientService patientService;
    private ProceduresAndCuresRepository proceduresAndCuresRepository;
    private AppointmentService appointmentService;
    private AppointmentRepository appointmentRepository;
    private EventService eventService;
    private PatientRepository patientRepository;

    @Autowired
    public AppointmentController (PatientService patientService,
                                  AppointmentService appointmentService,
                                  ProceduresAndCuresRepository proceduresAndCuresRepository,
                                  EventService eventService,
                                  PatientRepository patientRepository,
                                  AppointmentRepository appointmentRepository) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.proceduresAndCuresRepository = proceduresAndCuresRepository;
        this.eventService = eventService;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/working-with-patients/{patientId}/{appointmentId}/cancel")
    public String cancelAppointment(@PathVariable(value = "appointmentId") long appointmentId,
                                    @PathVariable(value = "patientId") long patientId,
                                    Model model) {
        appointmentService.cancelAppointment(appointmentId);
        return "redirect:/working-with-patients/" + patientId;
    }


    @GetMapping("/working-with-patients/{id}/add-procedure")
    public String addProcedure(@PathVariable(value = "id") long id, Model model) {
        PatientDTO result = patientService.getPatientDTObyID(id);
        model.addAttribute("patient", result);
        List<ProceduresAndCures> proceduresAndCures =
                proceduresAndCuresRepository
                        .findAllByTypeOfAppointment(PROCEDURE);
        model.addAttribute("procedures", proceduresAndCures);
        return "add-procedure";
    }

    @PostMapping("/working-with-patients/{id}/add-procedure")
    public String addProcedurePost(@PathVariable(value = "id") long id,
                                   @RequestParam String procedure,
                                   @RequestParam(value = "weekDay[]") String[] weekdays,
                                   @RequestParam(value = "time[]") String[] time,
                                   @RequestParam String period,
                                   Model model) {

        appointmentService.createAppointment(id, procedure, weekdays, time, period, "0", PROCEDURE);
        return "redirect:/working-with-patients/" + id;
    }

    @GetMapping("/working-with-patients/{id}/add-cure")
    public String addCure(@PathVariable(value = "id") long id, Model model) {
        PatientDTO result = patientService.getPatientDTObyID(id);
        model.addAttribute("patient", result);
        List<ProceduresAndCures> proceduresAndCures =
                proceduresAndCuresRepository
                        .findAllByTypeOfAppointment(CURE);
        model.addAttribute("cures", proceduresAndCures);
        return "add-cure";
    }


    @PostMapping("/working-with-patients/{id}/add-cure")
    public String addCurePost(@PathVariable(value = "id") long id,
                                   @RequestParam String cure,
                                   @RequestParam String dose,
                                   @RequestParam(value = "weekDay[]") String[] weekdays,
                                   @RequestParam(value = "time[]") String[] time,
                                   @RequestParam String period,
                                   Model model) {

        appointmentService.createAppointment(id, cure, weekdays, time, period, dose, CURE);
        return "redirect:/working-with-patients/" + id;
    }




}
