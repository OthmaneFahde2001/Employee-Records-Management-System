package com.Fahde.AuditLog.Repository;

import com.Fahde.AuditLog.Entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
