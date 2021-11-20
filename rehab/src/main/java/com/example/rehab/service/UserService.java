package com.example.rehab.service;

import com.example.rehab.models.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {


    void createUser(UserDTO userDTO);

    List<UserDTO> findAllDoctors();

    UserDTO findByUserName(String username);

    String getUserName();

}
