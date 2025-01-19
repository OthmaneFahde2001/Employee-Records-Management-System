package com.Fahde.Employee.Mapper;


import com.Fahde.Employee.DTO.EmployeeDTO;
import com.Fahde.Employee.Entity.Employee;
import com.Fahde.auth.Entity.User;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class EmployeeMapper {

    public EmployeeDTO toDto(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setJobTitle(employee.getJobTitle());
        dto.setDepartment(employee.getDepartment());
        dto.setHireDate(employee.getHireDate());
        dto.setEmploymentStatus(employee.getEmploymentStatus());
        dto.setContactInfo(employee.getContactInfo());
        dto.setAddress(employee.getAddress());
        return dto;
    }

    public Employee toEntity(EmployeeDTO dto) {
        return Employee.builder()
                .fullName(dto.getFullName())
                .employeeId(dto.getEmployeeId())
                .jobTitle(dto.getJobTitle())
                .department(dto.getDepartment())
                .hireDate(new Date())
                .employmentStatus(dto.getEmploymentStatus())
                .contactInfo(dto.getContactInfo())
                .address(dto.getAddress())
                .build();
    }
}
