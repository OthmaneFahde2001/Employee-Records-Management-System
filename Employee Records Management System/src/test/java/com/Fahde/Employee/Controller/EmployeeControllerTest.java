package com.Fahde.Employee.Controller;

import com.Fahde.Employee.DTO.EmployeeDTO;
import com.Fahde.Employee.Service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    @WithMockUser(authorities = "admin:read")
    void shouldGetAllEmployeesForAdminRole() throws Exception {
        Mockito.when(employeeService.getAllEmployees(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/employees")
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(authorities = "hr:create")
    void shouldCreateEmployeeForHRRole() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFullName("John Doe");

        Mockito.when(employeeService.createEmployee(Mockito.any(EmployeeDTO.class), anyString()))
                .thenReturn(employeeDTO);

        mockMvc.perform(post("/api/v1/employees")
                        .header("Authorization", "Bearer dummy-token")
                        .content("{\"fullName\": \"John Doe\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    @WithMockUser(authorities = "admin:delete")
    void shouldDeleteEmployeeForAdminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/1")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "user:read")
    void shouldReturnForbiddenForUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isForbidden());
    }
}
