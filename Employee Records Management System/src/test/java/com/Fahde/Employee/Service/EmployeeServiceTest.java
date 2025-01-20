package com.Fahde.Employee.Service;

import com.Fahde.Employee.DTO.EmployeeDTO;
import com.Fahde.Employee.Entity.Employee;
import com.Fahde.Employee.Mapper.EmployeeMapper;
import com.Fahde.Employee.Repository.EmployeeRepository;
import com.Fahde.auth.Entity.User;
import com.Fahde.auth.Repository.UserRepository;
import com.Fahde.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {

    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    EmployeeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(employeeRepository, null, employeeMapper, jwtService, userRepository);
    }

    @Test
    void shouldReturnAllEmployeesForAdminRole() {
        Mockito.when(jwtService.extractRole(anyString())).thenReturn("ADMINISTRATOR");
        Mockito.when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        var employees = employeeService.getAllEmployees("dummy-token");
        assertTrue(employees.isEmpty());
    }

    @Test
    void shouldReturnEmployeesForManagerRole() {
        when(jwtService.extractRole(anyString())).thenReturn("MANAGER");
        when(employeeRepository.findAll(any(Specification.class)))
                .thenReturn(Collections.emptyList());

        List<EmployeeDTO> employees = employeeService.getAllEmployees("dummy-token");
        assertTrue(employees.isEmpty());
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFullName("John Doe");

        Employee employee = new Employee();
        employee.setFullName("John Doe");

        User user = new User();
        user.setId(1);
        user.setUsername("Manager Name");

        Mockito.when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        Mockito.when(employeeMapper.toEntity(any(EmployeeDTO.class))).thenReturn(employee);
        Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Mockito.when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDTO);

        var createdEmployee = employeeService.createEmployee(employeeDTO, "dummy-token");

        assertEquals("John Doe", createdEmployee.getFullName());
    }

    @Test
    void shouldThrowExceptionWhenUnauthorizedRoleTriesToFetchEmployees() {
        Mockito.when(jwtService.extractRole(anyString())).thenReturn("UNAUTHORIZED_ROLE");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getAllEmployees("dummy-token");
        });

        assertEquals("Unauthorized role: UNAUTHORIZED_ROLE", exception.getMessage());
    }

    @Test
    void shouldUpdateEmployeeSuccessfully() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFullName("Updated Name");

        Employee employee = new Employee();
        employee.setId(1);
        employee.setFullName("Original Name");

        Mockito.when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Mockito.when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDTO);

        var updatedEmployee = employeeService.updateEmployee(1, employeeDTO, "dummy-token");
        assertEquals("Updated Name", updatedEmployee.getFullName());
    }
}
