package com.example.rehab.controllers;

import com.example.rehab.config.security.WebSecurityConfig;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.service.DispatcherService;
import com.example.rehab.service.PatientService;
import com.example.rehab.service.impl.AppointmentServiceImpl;
import com.example.rehab.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(PatientsController.class)
@Import(WebSecurityConfig.class)
class PatientsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PatientServiceImpl patientService;

    @MockBean
    DataSource dataSource;


    @MockBean
    AppointmentServiceImpl appointmentService;


    @Test
    @WithMockUser(username="admin",roles={"ADMIN"})
    public void patientsShouldReturnPatientList() throws Exception {
        List<PatientDTO> list = new ArrayList<>();
        list.add(new PatientDTO("Test",123L,"diagnosis","doctor", PatientStatus.ON_TREATMENT));
        when(patientService.findAll()).thenReturn(list);
        this.mockMvc.perform(get("/patients")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Test")));
    }

    @Test
    public void patientsShouldRedirectToLogin() throws Exception {
        this.mockMvc.perform(get("/patients")).andExpect(status().is3xxRedirection());
    }
}