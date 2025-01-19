package com.Fahde.Employee.DTO;

import com.Fahde.Employee.Entity.EmploymentStatus;
import com.Fahde.auth.Entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class EmployeeDTO {
    private Integer id;
    private String fullName;
    private String employeeId;
    private String jobTitle;
    private String department;
    private Date hireDate;
    private EmploymentStatus employmentStatus;
    private String contactInfo;
    private String address;
}
