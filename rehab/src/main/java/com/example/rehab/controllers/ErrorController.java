package com.example.rehab.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Slf4j
@Controller
public class ErrorController {


    @GetMapping("/403")
    public String accessDenied() {
        log.warn("Access denied");
        return "403";
    }



}


