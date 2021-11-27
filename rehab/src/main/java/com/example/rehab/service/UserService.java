package com.example.rehab.service;

import com.example.rehab.models.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    /**
     * Saves user in database
     * @author Dmitriy Zhiburtovich
     */
    void createUser(UserDTO userDTO);

    /**
     * Returns all users with 'DOCTOR' user role
     * @author Dmitriy Zhiburtovich
     */
    List<UserDTO> findAllDoctors();

    /**
     * Finds user with username and converts to dto
     * @author Dmitriy Zhiburtovich
     */
    UserDTO findByUserName(String username);

    /**
     * Returns authenticated username
     * @author Dmitriy Zhiburtovich
     */
    String getUserName();

    /**
     * Checks that user with this name exists in db or not
     * @author Dmitriy Zhiburtovich
     */
    boolean userExists(UserDTO userDTO);

}
