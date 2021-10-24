package com.example.rehab.controllers;

import com.example.rehab.models.Event;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.repo.EventRepository;
import com.example.rehab.service.EventService;
import com.example.rehab.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import static com.example.rehab.models.enums.EventStatus.COMPLETED;
import static com.example.rehab.models.enums.EventStatus.CANCELED;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EventsController {

    private EventService eventService;
    private EventRepository eventRepository;
    private Mapper mapper;

    @Autowired
    public EventsController(EventService eventService, EventRepository eventRepository, Mapper mapper) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    @GetMapping("/events")
    public String events(Model model) {
        return findPaginated(1,"date","asc", model);
    }

    @GetMapping("/events/{pageNumber}")
    public String findPaginated(@PathVariable (value = "pageNumber") int pageNumber,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {

        Page<EventDTO> page = eventService.findPaginated(pageNumber, 10, sortField, sortDir);

       /* List<EventDTO> resultList = eventList.stream()
                .map(mapper::convertEventToDTO)
                .collect(Collectors.toList());*/

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("events", page.getContent());
        return "events";
    }

    @GetMapping("/events/{eventId}/complete")
    public String completeEvent(@PathVariable(value = "eventId") int eventId,
                                Model model) {
        eventService.completeEvent(eventId);
        return "redirect:/events";
    }

    @GetMapping("/events/{eventId}/cancel")
    public String cancelEvent(@PathVariable(value = "eventId") int eventId,
                              Model model) {
        return "cancel-event";
    }

    @PostMapping("/events/{eventId}/cancel")
    public String cancelEventPost(@PathVariable(value = "eventId") int eventId,
                                  @RequestParam String message,
                                  Model model) {
       // model.addAttribute("eventId",eventId);
        eventService.cancelEvent(eventId,message);
        return "redirect:/events";

    }

    @PostMapping("/events/")
    public String filterByPatient(@RequestParam String name) {
        return "redirect:/events/patient/" + name;
    }

    @GetMapping("/events/patient/{name}")
    public String getEventsPyPatient(@PathVariable String name,Model model) {
        List<EventDTO> events = eventService.findByPatient(name);
        model.addAttribute("patientName",name);
        model.addAttribute("events", events);
        return "events-by-patient";
    }

    @GetMapping("/events/today")
    public String eventsToday(Model model) {
        List<EventDTO> events = eventService.findAllToday();
        model.addAttribute("events",events);
        return "events-today";
    }

    @GetMapping("/events/recent")
    public String eventsRecent(Model model) {
        List<EventDTO> events = eventService.findAllRecent();
        model.addAttribute("events",events);
        return "events-recent";
    }


}
