package com.example.rehab.controllers;


import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.service.ProceduresAndCuresService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
public class AdminController {

    ProceduresAndCuresService proceduresAndCuresService;

    @Autowired
    public AdminController(ProceduresAndCuresService proceduresAndCuresService) {
        this.proceduresAndCuresService = proceduresAndCuresService;
    }

    @GetMapping("/admin")
    public String adminPanel() {
        return "admin";
    }

    @GetMapping("/admin/procedures-and-cures")
    public String proceduresAndCures(Model model) {
        model.addAttribute("item",new ProceduresAndCures());
        return "procedures-and-cures";
    }

    @PostMapping("/admin/procedures-and-cures")
    public String addProceduresAndCures(@RequestParam String type,
                                        @RequestParam String name,
                                        Model model) {
        proceduresAndCuresService.addValue(type, name);
        return "redirect:/admin";
    }


    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model) {
        log.warn("Item already exists");
        String message = "Item with this name already exists";
        model.addAttribute("message",message);
        model.addAttribute("path","/admin");
        return "error-page";
    }

}
