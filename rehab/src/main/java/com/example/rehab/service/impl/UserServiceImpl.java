package com.example.rehab.service.impl;

import com.example.rehab.models.User;
import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.models.enums.UserRole;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.UserService;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

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

}
