package com.Fahde.Employee.Service;

import com.Fahde.AuditLog.Entity.AuditLog;
import com.Fahde.AuditLog.Repository.AuditLogRepository;
import com.Fahde.Employee.DTO.EmployeeDTO;
import com.Fahde.Employee.Entity.Employee;
import com.Fahde.Employee.Filter.EmployeeSpecifications;
import com.Fahde.Employee.Mapper.EmployeeMapper;
import com.Fahde.Employee.Repository.EmployeeRepository;
import com.Fahde.auth.Entity.User;
import com.Fahde.auth.Repository.UserRepository;
import com.Fahde.config.JwtService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuditLogRepository auditLogRepository;
    private final EmployeeMapper employeeMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public List<EmployeeDTO> getAllEmployees(String token) {
        Integer userId = jwtService.extractId(token);
        String userRole = jwtService.extractRole(token);
        log.info("Fetching employees for user ID: {} with role: {}", userId, userRole);

        List<Employee> employees;

        if ("MANAGER".equalsIgnoreCase(userRole)) {
            log.debug("Fetching employees for manager role");
            employees = employeeRepository.findAll(EmployeeSpecifications.hasDepartment(userRole));
        } else if ("ADMINISTRATOR".equalsIgnoreCase(userRole) || "HR_PERSONNEL".equalsIgnoreCase(userRole)) {
            log.debug("Fetching all employees for admin/HR role");
            employees = employeeRepository.findAll();
        } else {
            log.error("Unauthorized access attempt with role: {}", userRole);
            throw new RuntimeException("Unauthorized role: " + userRole);
        }

        log.info("Retrieved {} employees", employees.size());
        return employees.stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO, String token) {
        String username = jwtService.extractUsername(token);
        Integer userId= jwtService.extractId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));


        try {
            Employee employee = employeeMapper.toEntity(employeeDTO);
            employee.setHireDate(new Date());
            //employee.setUser(user);
            Employee savedEmployee = employeeRepository.save(employee);
            log.info("Successfully created employee with ID: {}", savedEmployee.getId());

            saveAuditLog(savedEmployee, username, "Created Employee", null, savedEmployee.toString());
            return employeeMapper.toDto(savedEmployee);
        } catch (Exception e) {
            log.error("Error creating employee: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create employee: " + e.getMessage());
        }
    }

    @Transactional
    public EmployeeDTO updateEmployee(int id, EmployeeDTO employeeDTO, String token) {
        String username = jwtService.extractUsername(token);
        String userRole = jwtService.extractRole(token);
        log.info("Updating employee ID: {} by user: {} with role: {}", id, username, userRole);

        try {
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

            Map<String, Object> beforeState = new HashMap<>();
            beforeState.put("id", existingEmployee.getId());
            beforeState.put("fullName", existingEmployee.getFullName());
            beforeState.put("jobTitle", existingEmployee.getJobTitle());
            beforeState.put("department", existingEmployee.getDepartment());
            beforeState.put("employmentStatus", existingEmployee.getEmploymentStatus());
            beforeState.put("contactInfo", existingEmployee.getContactInfo());
            beforeState.put("address", existingEmployee.getAddress());

            if ("MANAGER".equals(userRole) && !existingEmployee.getDepartment().equals(employeeDTO.getDepartment())) {
                log.error("Manager attempted to update employee from different department. Manager: {}", username);
                throw new RuntimeException("Managers can only update employees in their own department.");
            }

            existingEmployee.setFullName(employeeDTO.getFullName());
            existingEmployee.setJobTitle(employeeDTO.getJobTitle());
            existingEmployee.setDepartment(employeeDTO.getDepartment());
            existingEmployee.setEmploymentStatus(employeeDTO.getEmploymentStatus());
            existingEmployee.setContactInfo(employeeDTO.getContactInfo());
            existingEmployee.setAddress(employeeDTO.getAddress());

            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            log.info("Successfully updated employee ID: {}", id);

            ObjectMapper objectMapper = new ObjectMapper();
            String beforeValue = objectMapper.writeValueAsString(beforeState);

            Map<String, Object> afterState = new HashMap<>();
            afterState.put("id", updatedEmployee.getId());
            afterState.put("fullName", updatedEmployee.getFullName());
            afterState.put("jobTitle", updatedEmployee.getJobTitle());
            afterState.put("department", updatedEmployee.getDepartment());
            afterState.put("employmentStatus", updatedEmployee.getEmploymentStatus());
            afterState.put("contactInfo", updatedEmployee.getContactInfo());
            afterState.put("address", updatedEmployee.getAddress());

            String afterValue = objectMapper.writeValueAsString(afterState);

            saveAuditLog(updatedEmployee, username, "Updated Employee", beforeValue, afterValue);

            return employeeMapper.toDto(updatedEmployee);
        } catch (Exception e) {
            log.error("Error updating employee ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update employee: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteEmployee(String token,int idEmployee) {
        int idUser=jwtService.extractId(token);
        Employee existingEmployee = employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + idEmployee));

        User user=userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + idUser));


        saveAuditLog(existingEmployee,user.getUsername(), "Deleted Employee", existingEmployee.toString(), null);
        employeeRepository.delete(existingEmployee);
    }



    private void saveAuditLog(Employee employee, String username, String action, String beforeValue, String afterValue) {
        log.debug("Saving audit log for employee ID: {}, action: {}", employee.getId(), action);

        ObjectMapper objectMapper = new ObjectMapper();
        String afterValueJson = null;
        try {
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            afterValueJson = objectMapper.writeValueAsString(employee);
        } catch (JsonProcessingException e) {
            log.error("Error serializing employee data for audit log: {}", e.getMessage(), e);
            afterValueJson = "Error serializing employee data: " + employee.toString();
        }

        AuditLog auditLog = AuditLog.builder()
                .employeeId(employee.getId())
                .employeeFullName(employee.getFullName())
                .user(employee.getUser())
                .action(action)
                .changedBy(username)
                .changeTimestamp(new Date())
                .beforeValue(beforeValue)
                .afterValue(afterValueJson)
                .build();

        try {
            auditLogRepository.save(auditLog);
            log.debug("Successfully saved audit log for employee ID: {}", employee.getId());
        } catch (Exception e) {
            log.error("Error saving audit log for employee ID {}: {}", employee.getId(), e.getMessage(), e);
            // You might want to throw a custom exception or implement a retry mechanism
        }
    }

    public List<EmployeeDTO> searchEmployees(String name, String employeeId, String department, String jobTitle, String employmentStatus, Date hiredAfter, Date hiredBefore) {
        Specification<Employee> spec = Specification
                .where(EmployeeSpecifications.hasName(name))
                .and(EmployeeSpecifications.hasEmployeeId(employeeId))
                .and(EmployeeSpecifications.hasDepartment(department))
                .and(EmployeeSpecifications.hasJobTitle(jobTitle))
                .and(EmployeeSpecifications.hasEmploymentStatus(employmentStatus))
                .and(EmployeeSpecifications.hiredAfter(hiredAfter))
                .and(EmployeeSpecifications.hiredBefore(hiredBefore));

        return employeeRepository.findAll(spec).stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }
}