package com.example.rehab.controllers;

import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.repo.ProceduresAndCuresRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.ProceduresAndCuresService;
import com.example.rehab.service.impl.PatientServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import static com.example.rehab.models.enums.TypeOfAppointment.PROCEDURE;
import static com.example.rehab.models.enums.TypeOfAppointment.CURE;

import java.util.List;

@Slf4j
@Controller
public class AppointmentController {

    private final PatientServiceImpl patientService;
    private final AppointmentService appointmentService;
    private final ProceduresAndCuresService proceduresAndCuresService;

    @Autowired
    public AppointmentController (PatientServiceImpl patientService,
                                  AppointmentService appointmentService,
                                  ProceduresAndCuresService proceduresAndCuresService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.proceduresAndCuresService = proceduresAndCuresService;
    }

    @GetMapping("/patients/{patientId}/{appointmentId}/cancel")
    public String cancelAppointment(@PathVariable(value = "appointmentId") long appointmentId,
                                    @PathVariable(value = "patientId") long patientId,
                                    Model model) {
        appointmentService.cancelAppointment(appointmentId);
        return "redirect:/patients/" + patientId;
    }

    @GetMapping("/patients/{patientId}/{appointmentId}/hide")
    public String hideAppointment(@PathVariable(value = "appointmentId") long appointmentId,
                                    @PathVariable(value = "patientId") long patientId,
                                    Model model) {
        appointmentService.hideAppointment(appointmentId);
        return "redirect:/patients/" + patientId;
    }

    @GetMapping("/patients/{patientId}/{appointmentId}/edit-procedure")
    public String editProcedure(@PathVariable(value = "appointmentId") long appointmentId,
                                @PathVariable(value = "patientId") long patientId,
                                Model model) {
        model.addAttribute("appointmentId",appointmentId);
        model.addAttribute("patientId", patientId);
        return "edit-procedure";
    }

    @PostMapping("/patients/{patientId}/{appointmentId}/edit-procedure")
    public String editProcedurePost(@PathVariable(value = "appointmentId") long appointmentId,
                                    @PathVariable(value = "patientId") long patientId,
                                    @RequestParam(value = "weekDay[]") String[] weekdays,
                                    @RequestParam(value = "time[]") String[] time,
                                    @RequestParam String period,
                                    Model model) {
        appointmentService.updateAppointment(appointmentId,weekdays,time,period,"0");
        return "redirect:/patients/" + patientId;
    }

    @GetMapping("/patients/{patientId}/{appointmentId}/edit-cure")
    public String editCure(@PathVariable(value = "appointmentId") long appointmentId,
                                @PathVariable(value = "patientId") long patientId,
                                Model model) {
        model.addAttribute("appointmentId",appointmentId);
        model.addAttribute("patientId", patientId);
        return "edit-cure";
    }

    @PostMapping("/patients/{patientId}/{appointmentId}/edit-cure")
    public String editCurePost(@PathVariable(value = "appointmentId") long appointmentId,
                                    @PathVariable(value = "patientId") long patientId,
                                    @RequestParam String dose,
                                    @RequestParam(value = "weekDay[]") String[] weekdays,
                                    @RequestParam(value = "time[]") String[] time,
                                    @RequestParam String period,
                                    Model model) {
        appointmentService.updateAppointment(appointmentId,weekdays,time,period,dose);
        return "redirect:/patients/" + patientId;
    }

    @GetMapping("/patients/{id}/add-procedure")
    public String addProcedure(@PathVariable(value = "id") long id, Model model) {
        model.addAttribute("patient", patientService.getPatientByID(id));
        model.addAttribute("procedures", proceduresAndCuresService.findAllProcedures());
        return "add-procedure";
    }

    @PostMapping("/patients/{id}/add-procedure")
    public String addProcedurePost(@PathVariable(value = "id") long id,
                                   @RequestParam String procedure,
                                   @RequestParam(value = "weekDay[]") String[] weekdays,
                                   @RequestParam(value = "time[]") String[] time,
                                   @RequestParam String period,
                                   Model model) {
        appointmentService.createAppointment(id, procedure, weekdays, time, period, "0", PROCEDURE);
        return "redirect:/patients/" + id;
    }

    @GetMapping("/patients/{id}/add-cure")
    public String addCure(@PathVariable(value = "id") long id, Model model) {
        model.addAttribute("patient", patientService.getPatientByID(id));
        model.addAttribute("cures", proceduresAndCuresService.findAllCures());
        return "add-cure";
    }


    @PostMapping("/patients/{id}/add-cure")
    public String addCurePost(@PathVariable(value = "id") long id,
                                   @RequestParam String cure,
                                   @RequestParam String dose,
                                   @RequestParam(value = "weekDay[]") String[] weekdays,
                                   @RequestParam(value = "time[]") String[] time,
                                   @RequestParam String period,
                                   Model model) {
            appointmentService.createAppointment(id, cure, weekdays, time, period, dose, CURE);
        return "redirect:/patients/" + id;
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public String handleException(MissingServletRequestParameterException e, Model model) {
        log.error("Input data error");
        String message = "Please, check your input data";
        model.addAttribute("message",message);
        return "error-page";
    }






}
