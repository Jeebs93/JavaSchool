package com.example.rehab.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Slf4j
@Controller
public class ErrorController {


    @GetMapping("/403")
    public String accessDenied() {
        log.warn("Access denied");
        return "error/403";
    }





}


