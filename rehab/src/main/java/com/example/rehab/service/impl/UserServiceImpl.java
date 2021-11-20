package com.example.rehab.service.impl;

import com.example.rehab.models.User;
import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.models.enums.UserRole;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.UserService;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
     UserDTO user = mapper.convertUserToDTO(userRepository.findByUsername(username));
     return user;
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
