package com.example.rehab.controllers;

import com.example.rehab.models.User;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.UserService;
import com.example.rehab.service.impl.UserServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public RegistrationController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user",new UserDTO());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid @ModelAttribute(name="user") UserDTO userDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "registration";
        }
        if (userService.userExists(userDTO)) {
            model.addAttribute("message", "User exists!");
            return "registration";
        }
        userService.createUser(userDTO);
        return "redirect:/";
    }

}
