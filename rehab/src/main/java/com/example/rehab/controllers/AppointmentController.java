package com.example.rehab.controllers;

import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.ProceduresAndCuresRepository;
import com.example.rehab.service.AppointmentService;
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

@Controller
public class AppointmentController {

    private PatientService patientService;
    private ProceduresAndCuresRepository proceduresAndCuresRepository;
    private AppointmentService appointmentService;

    @Autowired
    public AppointmentController (PatientService patientService,
                                  AppointmentService appointmentService,
                                  ProceduresAndCuresRepository proceduresAndCuresRepository) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.proceduresAndCuresRepository = proceduresAndCuresRepository;
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
        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> resultTime = Arrays.asList(time);
        AppointmentDTO appointmentDTO = new AppointmentDTO(id, procedure, resultWeekDays,
                resultTime, period, "0", PROCEDURE);
        appointmentService.createAppointment(appointmentDTO);

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
        List<String> resultWeekDays = Arrays.asList(weekdays);
        List<String> resultTime = Arrays.asList(time);
        AppointmentDTO appointmentDTO = new AppointmentDTO(id, cure, resultWeekDays,
                resultTime, period, dose, CURE);
        appointmentService.createAppointment(appointmentDTO);

        return "redirect:/working-with-patients/" + id;
    }




}
