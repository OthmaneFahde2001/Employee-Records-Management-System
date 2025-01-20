package com.Fahde.Employee.Mapper;

import com.Fahde.Employee.DTO.EmployeeDTO;
import com.Fahde.Employee.Entity.Employee;
import com.Fahde.Employee.Entity.EmploymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeMapperTest {

    private EmployeeMapper employeeMapper;

    @BeforeEach
    void setUp() {
        employeeMapper = new EmployeeMapper();
    }

    @Test
    void testToDto() {
        Employee employee = Employee.builder()
                .id(1)
                .fullName("John Doe")
                .employeeId("EMP123")
                .jobTitle("Software Engineer")
                .department("IT")
                .hireDate(new Date())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .contactInfo("john.doe@example.com")
                .address("123 Main St, City, Country")
                .build();

        EmployeeDTO dto = employeeMapper.toDto(employee);

        assertEquals(employee.getId(), dto.getId());
        assertEquals(employee.getFullName(), dto.getFullName());
        assertEquals(employee.getEmployeeId(), dto.getEmployeeId());
        assertEquals(employee.getJobTitle(), dto.getJobTitle());
        assertEquals(employee.getDepartment(), dto.getDepartment());
        assertEquals(employee.getHireDate(), dto.getHireDate());
        assertEquals(employee.getEmploymentStatus(), dto.getEmploymentStatus());
        assertEquals(employee.getContactInfo(), dto.getContactInfo());
        assertEquals(employee.getAddress(), dto.getAddress());
    }

    @Test
    void testToEntity() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(1);
        dto.setFullName("John Doe");
        dto.setEmployeeId("EMP123");
        dto.setJobTitle("Software Engineer");
        dto.setDepartment("IT");
        dto.setHireDate(new Date());
        dto.setEmploymentStatus(EmploymentStatus.ACTIVE);
        dto.setContactInfo("john.doe@example.com");
        dto.setAddress("123 Main St, City, Country");

        Employee entity = employeeMapper.toEntity(dto);

        assertEquals(dto.getFullName(), entity.getFullName());
        assertEquals(dto.getEmployeeId(), entity.getEmployeeId());
        assertEquals(dto.getJobTitle(), entity.getJobTitle());
        assertEquals(dto.getDepartment(), entity.getDepartment());
        assertEquals(dto.getEmploymentStatus(), entity.getEmploymentStatus());
        assertEquals(dto.getContactInfo(), entity.getContactInfo());
        assertEquals(dto.getAddress(), entity.getAddress());
    }
}
