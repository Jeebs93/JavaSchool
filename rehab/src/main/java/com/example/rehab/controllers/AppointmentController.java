package com.example.rehab.controllers;

import com.example.rehab.models.Appointment;
import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.models.dto.AppointmentDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.repo.AppointmentRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.repo.ProceduresAndCuresRepository;
import com.example.rehab.service.AppointmentService;
import com.example.rehab.service.EventService;
import com.example.rehab.service.PatientService;
import com.example.rehab.service.mapper.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
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

    private PatientService patientService;
    private ProceduresAndCuresRepository proceduresAndCuresRepository;
    private AppointmentService appointmentService;
    private Mapper mapper;

    @Autowired
    public AppointmentController (PatientService patientService,
                                  AppointmentService appointmentService,
                                  ProceduresAndCuresRepository proceduresAndCuresRepository,
                                  EventService eventService,
                                  PatientRepository patientRepository,
                                  AppointmentRepository appointmentRepository,
                                  Mapper mapper) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.proceduresAndCuresRepository = proceduresAndCuresRepository;

        this.mapper = mapper;
    }

    @GetMapping("/working-with-patients/{patientId}/{appointmentId}/cancel")
    public String cancelAppointment(@PathVariable(value = "appointmentId") long appointmentId,
                                    @PathVariable(value = "patientId") long patientId,
                                    Model model) {
        appointmentService.cancelAppointment(appointmentId);
        return "redirect:/working-with-patients/" + patientId;
    }

    @GetMapping("/working-with-patients/{patientId}/{appointmentId}/edit-procedure")
    public String editProcedure(@PathVariable(value = "appointmentId") long appointmentId,
                                @PathVariable(value = "patientId") long patientId,
                                Model model) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);
        model.addAttribute("appointmentId",appointmentId);
        model.addAttribute("patientId", patientId);
        return "edit-procedure";
    }

    @PostMapping("/working-with-patients/{patientId}/{appointmentId}/edit-procedure")
    public String editProcedurePost(@PathVariable(value = "appointmentId") long appointmentId,
                                    @PathVariable(value = "patientId") long patientId,
                                    @RequestParam(value = "weekDay[]") String[] weekdays,
                                    @RequestParam(value = "time[]") String[] time,
                                    @RequestParam String period,
                                    Model model) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);
        AppointmentDTO appointmentDTO = mapper.convertAppointmentToDTO(appointment);
        appointmentService.updateAppointment(appointmentDTO,weekdays,time,period,"0");
        return "redirect:/working-with-patients/" + patientId;
    }

    @GetMapping("/working-with-patients/{patientId}/{appointmentId}/edit-cure")
    public String editCure(@PathVariable(value = "appointmentId") long appointmentId,
                                @PathVariable(value = "patientId") long patientId,
                                Model model) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);
        model.addAttribute("appointmentId",appointmentId);
        model.addAttribute("patientId", patientId);
        return "edit-cure";
    }

    @PostMapping("/working-with-patients/{patientId}/{appointmentId}/edit-cure")
    public String editCurePost(@PathVariable(value = "appointmentId") long appointmentId,
                                    @PathVariable(value = "patientId") long patientId,
                                    @RequestParam String dose,
                                    @RequestParam(value = "weekDay[]") String[] weekdays,
                                    @RequestParam(value = "time[]") String[] time,
                                    @RequestParam String period,
                                    Model model) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId); // to services
        AppointmentDTO appointmentDTO = mapper.convertAppointmentToDTO(appointment);
        appointmentService.updateAppointment(appointmentDTO,weekdays,time,period,dose);
        return "redirect:/working-with-patients/" + patientId;
    }

    @GetMapping("/working-with-patients/{id}/add-procedure")
    public String addProcedure(@PathVariable(value = "id") long id, Model model) {
        model.addAttribute("patient", patientService.getPatientDTObyID(id));
        model.addAttribute("procedures", proceduresAndCuresRepository
                .findAllByTypeOfAppointment(PROCEDURE));
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

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public String handleException(MissingServletRequestParameterException e, Model model) {
        log.error("Input data error");
        String message = "Please, check your input data";
        model.addAttribute("message",message);
        return "error-page";
    }






}
