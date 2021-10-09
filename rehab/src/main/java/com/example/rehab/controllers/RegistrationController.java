package com.example.rehab.controllers;

import com.example.rehab.models.User;
import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.models.enums.UserRole;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.UserService;
import com.example.rehab.service.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {

    private UserRepository userRepository;
    private UserService userService;
    private Mapper mapper;

    @Autowired
    public RegistrationController(UserRepository userRepository,UserService userService, Mapper mapper) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.mapper = mapper;

    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(UserDTO userDTO, Map<String, Object> model) {
        User userFromDB = userRepository.findByUsername(userDTO.getUsername());

        if (userFromDB != null) {
            model.put("message", "User exists!");
            return "registration";
        }

        userService.createUser(userDTO);
        return "redirect:/login";
    }

}
