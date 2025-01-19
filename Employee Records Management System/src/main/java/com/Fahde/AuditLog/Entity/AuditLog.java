package com.Fahde.AuditLog.Entity;

import com.Fahde.Employee.Entity.Employee;
import com.Fahde.auth.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(name = "change_timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date changeTimestamp;

    @Column(name = "before_value", columnDefinition = "LONGTEXT")
    private String beforeValue;

    @Column(name = "after_value", columnDefinition = "LONGTEXT")
    private String afterValue;

    @Column(name = "employee_Id", nullable = false)
    private Integer employeeId;

    @Column(name = "employee_fullName", nullable = false)
    private String employeeFullName;
}
