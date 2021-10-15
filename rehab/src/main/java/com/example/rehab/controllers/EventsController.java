package com.example.rehab.controllers;

import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class EventsController {

    private EventService eventService;

    public EventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<EventDTO> events = eventService.findAll();
        model.addAttribute("events", events);
        return "events";
    }


}
