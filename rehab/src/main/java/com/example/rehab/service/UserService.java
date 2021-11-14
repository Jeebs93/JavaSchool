package com.example.rehab.service;

import com.example.rehab.models.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {


    void createUser(UserDTO userDTO);

}
