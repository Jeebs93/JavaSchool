package com.example.rehab.controllers;

import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.repo.PatientRepository;
import com.example.rehab.service.EventService;
import com.example.rehab.service.impl.PatientServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
public class EventsController {

    private final EventService eventService;
    private final PatientServiceImpl patientService;

    @Autowired
    public EventsController(EventService eventService, PatientServiceImpl patientService) {
        this.eventService = eventService;
        this.patientService = patientService;
    }

    @GetMapping("/events")
    public String events(Model model) {
        return getEventsPaginated(1,"date","asc", model);
    }

    @GetMapping("/events/{pageNumber}")
    public String getEventsPaginated(@PathVariable (value = "pageNumber") int pageNumber,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {

        Page<EventDTO> page = eventService.findPaginated(pageNumber, 10, sortField, sortDir);

        int current = page.getNumber() + 5;
        int begin = Math.max(1, current - 10);
        int end = Math.min(begin + 11, page.getTotalPages());

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("events", page.getContent());
        return "events";
    }

    @GetMapping("/events/patient/{id}")
    public String eventsByPatient(Model model,@PathVariable int id) {
        return getEventsPyPatientPaginated(id, 1, model);
    }

    @GetMapping("/events/patient/{id}/{pageNumber}")
    public String getEventsPyPatientPaginated(@PathVariable int id,
                                              @PathVariable (value = "pageNumber") int pageNumber,
                                              Model model) {
        Page<EventDTO> page = eventService.findByPatientPaginated(pageNumber,10,id);
        int current = page.getNumber() + 5;
        int begin = Math.max(1, current - 10);
        int end = Math.min(begin + 11, page.getTotalPages());
        model.addAttribute("patientName",patientService.getPatientByID(id).getName());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("patientID",id);
        model.addAttribute("events", page.getContent());
        return "events-by-patient";
    }

    @GetMapping("/events/{eventId}/complete")
    public String completeEvent(@PathVariable(value = "eventId") int eventId,
                                Model model, HttpServletRequest request) {
        eventService.completeEvent(eventId);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/events/{eventId}/hide")
    public String hideEvent(@PathVariable(value="eventId") int eventId, HttpServletRequest request) {
        eventService.hideEvent(eventId);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/events/{eventId}/cancel")
    public String cancelEvent(@PathVariable(value = "eventId") int eventId,
                              HttpServletRequest request,
                              Model model) {
        String req=request.getHeader("Referer");
        model.addAttribute("request",req);
        return "cancel-event";
    }

    @PostMapping("/events/{eventId}/cancel")
    public String cancelEventPost(@PathVariable(value = "eventId") int eventId,
                                  @RequestParam String message,
                                  @RequestParam String request) {
        eventService.cancelEvent(eventId,message);
        return "redirect:" + request;

    }

    @PostMapping("/events/")
    public String filterByPatient(@RequestParam String name) {
        return patientService.isPatientAmbiguous(name) ?
                "redirect:/events/patient/ambiguous/" + name :
                "redirect:/events/patient/" + patientService.getIdByName(name);
    }

    @GetMapping("/events/patient/ambiguous/{name}")
    public String ambiguousPatient(@PathVariable String name, Model model) {
        List<PatientDTO> patients = patientService.getPatientsByName(name);
        model.addAttribute("patients", patients);
        return "ambiguous-patient";
    }

    @GetMapping("/events/today")
    public String eventsToday(Model model) {
        model.addAttribute("events",eventService.findAllToday());
        return "events-today";
    }

    @GetMapping("/events/recent")
    public String eventsRecent(Model model) {
        model.addAttribute("events",eventService.findAllRecent());
        return "events-recent";
    }

    @GetMapping("/events/appointment/{appointmentId}")
    public String appointmentEvents(@PathVariable(value = "appointmentId") int appointmentId, Model model) {
        model.addAttribute("events",eventService.findAllByAppointmentId(appointmentId));
        return "appointment-events";
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleException(NullPointerException e, Model model) {
        log.warn("Patient not found");
        String message = "Patient not found";
        model.addAttribute("message",message);
        model.addAttribute("path","/events");
        return "error-page";
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public String handleIndexException(IndexOutOfBoundsException e, Model model) {
        log.warn("Patient not found");
        String message = "Events not found";
        model.addAttribute("message",message);
        model.addAttribute("path","/events");
        return "error-page";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handlePatientsException(IllegalArgumentException e, Model model) {
        log.warn("Patient not found");
        String message = "Events not found";
        model.addAttribute("message",message);
        model.addAttribute("path","/events");
        return "error-page";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        log.warn("Events can not be displayed");
        String message = "Events can not be displayed";
        model.addAttribute("message",message);
        model.addAttribute("path","/");
        return "error-page";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model) {
        log.warn("The event could not be completed");
        String message = "The event could not be completed";
        model.addAttribute("message",message);
        model.addAttribute("path","/events");
        return "error-page";
    }



}
