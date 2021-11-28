package com.example.rehab.controllers;

import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    private final UserService userService;
    private static final String REGISTRATION = "registration";

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user",new UserDTO());
        return REGISTRATION;
    }

    @PostMapping("/registration")
    public String addUser(@Valid @ModelAttribute(name="user") UserDTO userDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return REGISTRATION;
        }
        if (userService.userExists(userDTO)) {
            model.addAttribute("message", "User exists!");
            return REGISTRATION;
        }
        userService.createUser(userDTO);
        return "redirect:/";
    }

}
