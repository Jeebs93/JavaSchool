package com.example.rehab.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @GetMapping("/403")
    public String accessDenied() {
        logger.info("Access denied");
        return "403";
    }



}


