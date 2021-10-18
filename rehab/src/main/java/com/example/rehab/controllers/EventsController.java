package com.example.rehab.controllers;

import com.example.rehab.models.Event;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.service.EventService;
import com.example.rehab.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EventsController {

    private EventService eventService;
    private Mapper mapper;

    @Autowired
    public EventsController(EventService eventService, Mapper mapper) {
        this.eventService = eventService;
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
        int pageSize = 10;

        Page<EventDTO> page = eventService.findPaginated(pageNumber, pageSize, sortField, sortDir);
        List<EventDTO> eventList = page.getContent();

       /* List<EventDTO> resultList = eventList.stream()
                .map(mapper::convertEventToDTO)
                .collect(Collectors.toList());*/

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("events", eventList);
        return "events";
    }


}
