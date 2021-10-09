package com.example.rehab.service;

import com.example.rehab.models.User;
import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.models.enums.UserRole;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final Mapper mapper;

    public void createUser(UserDTO userDTO) {
        User user = mapper.convertUserToEntity(userDTO);
        user.setActive(true);
        user.setUserRoles(Collections.singleton(UserRole.USER));
        userRepository.save(user);
    }

}
