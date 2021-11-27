package com.example.rehab.service.impl.unit;


import com.example.rehab.models.User;
import com.example.rehab.models.dto.UserDTO;
import com.example.rehab.models.enums.UserRole;
import com.example.rehab.repo.UserRepository;
import com.example.rehab.service.impl.UserServiceImpl;
import com.example.rehab.service.mapper.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceImplUnitTests {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    Mapper mapper = new Mapper(new ModelMapper());
    List<User> userList = Arrays.asList(
            new User(2L,"TestDoctor","TestPassword",true,new HashSet<>(Arrays.asList(UserRole.DOCTOR))),
            new User(3L,"TestNurse","TestPassword",true,new HashSet<>(Arrays.asList(UserRole.NURSE))),
            new User(4L,"TestDoctor2","TestPassword",true,new HashSet<>(Arrays.asList(UserRole.DOCTOR))),
            new User(5L,"TestAdmin","TestPassword",true,new HashSet<>(Arrays.asList(UserRole.ADMIN)))
    );

    private final UserServiceImpl userService = new UserServiceImpl(userRepository, mapper);

    UserDTO userDTO = new UserDTO(1L,"TestUser","TestPassword", UserRole.DOCTOR);

    @Test
    void testUserCreate() {
        Mockito.doAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            user.setId(1L);
            assertTrue(user.isActive());
            assertTrue(user.getUserRoles().contains(UserRole.DOCTOR));
            assertFalse(user.getUserRoles().contains(UserRole.NURSE));
            return null;
        }).when(userRepository).save(any(User.class));
        userService.createUser(userDTO);
    }

    @Test
    void testGetAllDoctors() {
        Mockito.when(userRepository.findAll()).thenReturn(userList);
        List<UserDTO> list = userService.findAllDoctors();
        assertEquals(2,list.size());
        assertEquals("TestDoctor",list.get(0).getUsername());
        assertEquals("TestDoctor2",list.get(1).getUsername());
    }

}
