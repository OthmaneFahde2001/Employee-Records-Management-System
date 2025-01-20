package com.Fahde.Employee.Controller;

import com.Fahde.Employee.DTO.EmployeeDTO;
import com.Fahde.Employee.Service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin:read', 'management:read', 'hr:read')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        List<EmployeeDTO> employees = employeeService.getAllEmployees(token);
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin:create', 'hr:create')")
    public ResponseEntity<EmployeeDTO> createEmployee(
            @Valid @RequestBody EmployeeDTO employeeDTO,
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO, token);
        return ResponseEntity.ok(createdEmployee);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:update', 'management:update', 'hr:update')")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable int id,
            @RequestBody EmployeeDTO employeeDTO,
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO, token);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:delete', 'hr:delete')")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable int id,
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        employeeService.deleteEmployee(token, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('admin:read', 'management:read', 'hr:read')")
    public ResponseEntity<List<EmployeeDTO>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String employmentStatus,
            @RequestParam(required = false) Date hiredAfter,
            @RequestParam(required = false) Date hiredBefore) {
        List<EmployeeDTO> employees = employeeService.searchEmployees(name, employeeId, department, jobTitle, employmentStatus, hiredAfter, hiredBefore);
        return ResponseEntity.ok(employees);
    }
}
