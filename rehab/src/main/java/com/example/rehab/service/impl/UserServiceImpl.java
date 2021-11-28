package com.example.rehab.service.impl;

import com.example.rehab.models.User;
import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.models.enums.UserRole;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.UserService;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final Mapper mapper;


    public void createUser(UserDTO userDTO) {
        User user = mapper.convertUserToEntity(userDTO);
        user.setActive(true);
        if (userDTO.getUserRole().equals(UserRole.DOCTOR)) {
            user.setUserRoles(Collections.singleton(UserRole.DOCTOR));
        } else user.setUserRoles(Collections.singleton(UserRole.NURSE));
        userRepository.save(user);
        long userID = user.getId();
        log.info(String.format("User with id %d has been created",userID));

    }

    public boolean userExists(UserDTO userDTO) {
        return userRepository.findByUsername(userDTO.getUsername()) != null;
    }

    public List<UserDTO> findAllDoctors() {
        List<User> doctorList = userRepository.findAll()
                .stream()
                .filter(user -> user.getUserRoles()
                        .contains(UserRole.DOCTOR))
                .collect(Collectors.toList());
        return doctorList.
                stream()
                .map(mapper::convertUserToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findByUserName(String username) {
        return mapper.convertUserToDTO(userRepository.findByUsername(username));
    }

    public String getUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username="";
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

}
