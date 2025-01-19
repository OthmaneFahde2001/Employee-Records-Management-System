package com.Fahde.Employee.Filter;



import com.Fahde.Employee.Entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class EmployeeSpecifications {

    public static Specification<Employee> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("fullName"), "%" + name + "%");
    }

    public static Specification<Employee> hasEmployeeId(String employeeId) {
        return (root, query, criteriaBuilder) ->
                employeeId == null ? null : criteriaBuilder.equal(root.get("employeeId"), employeeId);
    }

    public static Specification<Employee> hasDepartment(String department) {
        return (root, query, criteriaBuilder) ->
                department == null ? null : criteriaBuilder.equal(root.get("department"), department);
    }

    public static Specification<Employee> hasJobTitle(String jobTitle) {
        return (root, query, criteriaBuilder) ->
                jobTitle == null ? null : criteriaBuilder.equal(root.get("jobTitle"), jobTitle);
    }

    public static Specification<Employee> hasEmploymentStatus(String employmentStatus) {
        return (root, query, criteriaBuilder) ->
                employmentStatus == null ? null : criteriaBuilder.equal(root.get("employmentStatus"), employmentStatus);
    }

    public static Specification<Employee> hiredAfter(Date hireDate) {
        return (root, query, criteriaBuilder) ->
                hireDate == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("hireDate"), hireDate);
    }

    public static Specification<Employee> hiredBefore(Date hireDate) {
        return (root, query, criteriaBuilder) ->
                hireDate == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("hireDate"), hireDate);
    }
}