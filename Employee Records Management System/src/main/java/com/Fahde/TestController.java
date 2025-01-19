package com.Fahde;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    // GET endpoint - Accessible by HR_PERSONNEL and ADMINISTRATOR roles with READ permission
    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('HR_PERSONNEL','MANAGER', 'ADMINISTRATOR') and hasAnyAuthority('admin:read','management:read','hr:read')")
    public String getEmployees() {
        return "Access granted: Viewing all employees.";
    }

    // POST endpoint - Accessible by HR_PERSONNEL and ADMINISTRATOR roles with CREATE permission
    @PostMapping("/employees")
    @PreAuthorize("hasAnyRole('HR_PERSONNEL', 'ADMINISTRATOR') and hasAuthority('ADMIN_CREATE')")
    public String createEmployee() {
        return "Access granted: Employee created.";
    }

    // PUT endpoint - Accessible by MANAGER and ADMINISTRATOR roles with UPDATE permission
    @PutMapping("/employees/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMINISTRATOR') and hasAuthority('ADMIN_UPDATE')")
    public String updateEmployee(@PathVariable Integer id) {
        return "Access granted: Employee with ID " + id + " updated.";
    }

    // DELETE endpoint - Accessible by ADMINISTRATOR role with DELETE permission
    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') and hasAuthority('ADMIN_DELETE')")
    public String deleteEmployee(@PathVariable Integer id) {
        return "Access granted: Employee with ID " + id + " deleted.";
    }

    // GET endpoint for management - Accessible by MANAGER role with READ or UPDATE permissions
    @GetMapping("/management")
    @PreAuthorize("hasRole('MANAGER') and hasAnyAuthority('MANAGER_READ', 'MANAGER_UPDATE')")
    public String getManagementData() {
        return "Access granted: Management data.";
    }

    // POST endpoint for management - Accessible by MANAGER and ADMINISTRATOR roles with CREATE permission
    @PostMapping("/management")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMINISTRATOR') and hasAuthority('MANAGER_CREATE')")
    public String createManagementData() {
        return "Access granted: Management data created.";
    }

    // PUT endpoint for management - Accessible by MANAGER and ADMINISTRATOR roles with UPDATE permission
    @PutMapping("/management/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMINISTRATOR') and hasAuthority('MANAGER_UPDATE')")
    public String updateManagementData(@PathVariable Integer id) {
        return "Access granted: Management data with ID " + id + " updated.";
    }

    // DELETE endpoint for management - Accessible by ADMINISTRATOR only with DELETE permission
    @DeleteMapping("/management/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') and hasAuthority('MANAGER_DELETE')")
    public String deleteManagementData(@PathVariable Integer id) {
        return "Access granted: Management data with ID " + id + " deleted.";
    }

    // Public endpoint (no authentication required)
    @GetMapping("/public")
    public String getPublicData() {
        return "Access granted: Public data, no authentication required.";
    }
}
