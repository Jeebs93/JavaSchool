package com.example.rehab.controllers;

import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.service.EventService;
import com.example.rehab.service.impl.PatientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    private static final String EVENTS = "events";
    private static final String REFERER = "Referer";
    private static final String PATIENT_NOT_FOUND = "Patient not found";
    private static final String REDIRECT = "redirect:";
    private static final String MESSAGE = "message";
    private static final String ERROR_PAGE = "error-page";

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

        model.addAttribute(EVENTS, page.getContent());
        return EVENTS;
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
        model.addAttribute(EVENTS, page.getContent());
        return "events-by-patient";
    }

    @GetMapping("/events/complete/{eventId}")
    public String completeEvent(@PathVariable(value = "eventId") int eventId,
                                Model model, HttpServletRequest request) {
        eventService.completeEvent(eventId);
        return REDIRECT + request.getHeader(REFERER);
    }

    @GetMapping("/events/hide/{eventId}")
    public String hideEvent(@PathVariable(value="eventId") int eventId, HttpServletRequest request) {
        eventService.hideEvent(eventId);
        return REDIRECT + request.getHeader(REFERER);
    }

    @GetMapping("/events/cancel/{eventId}")
    public String cancelEvent(@PathVariable(value = "eventId") int eventId,
                              HttpServletRequest request,
                              Model model) {
        String req=request.getHeader(REFERER);
        model.addAttribute("request",req);
        return "cancel-event";
    }

    @PostMapping("/events/cancel/{eventId}")
    public String cancelEventPost(@PathVariable(value = "eventId") int eventId,
                                  @RequestParam String message,
                                  @RequestParam String request) {
        eventService.cancelEvent(eventId,message);
        return REDIRECT + request;

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
        model.addAttribute(EVENTS,eventService.findAllToday());
        return "events-today";
    }

    @GetMapping("/events/recent")
    public String eventsRecent(Model model) {
        model.addAttribute(EVENTS,eventService.findAllRecent());
        return "events-recent";
    }

    @GetMapping("/events/appointment/{appointmentId}")
    public String appointmentEvents(@PathVariable(value = "appointmentId") int appointmentId, Model model) {
        model.addAttribute(EVENTS,eventService.findAllByAppointmentId(appointmentId));
        return "appointment-events";
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleException(NullPointerException e, Model model) {
        log.warn(PATIENT_NOT_FOUND);
        String message = PATIENT_NOT_FOUND;
        model.addAttribute(MESSAGE,message);
        model.addAttribute("path","/" + EVENTS);
        return ERROR_PAGE;
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public String handleIndexException(IndexOutOfBoundsException e, Model model) {
        log.warn(PATIENT_NOT_FOUND);
        String message = "Events not found";
        model.addAttribute(MESSAGE,message);
        model.addAttribute("path","/"+EVENTS);
        return ERROR_PAGE;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handlePatientsException(IllegalArgumentException e, Model model) {
        log.warn(PATIENT_NOT_FOUND);
        String message = "Events not found";
        model.addAttribute(MESSAGE,message);
        model.addAttribute("path","/"+EVENTS);
        return ERROR_PAGE;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        log.warn("Events can not be displayed");
        String message = "Events can not be displayed";
        model.addAttribute(MESSAGE,message);
        model.addAttribute("path","/");
        return ERROR_PAGE;
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model) {
        log.warn("The event could not be completed");
        String message = "The event could not be completed";
        model.addAttribute(MESSAGE,message);
        model.addAttribute("path","/" +EVENTS);
        return ERROR_PAGE;
    }



}
