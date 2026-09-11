package com.hmbrandt.maintenance_service.dto;

import java.time.LocalDateTime;

public interface EquipmentIssueProjection {
    Long getId();
    Long getEquipmentId();
    Long getReferenceType();
    String getReportedBy();
    LocalDateTime getReportedAt();
    String getIssueType();
    String getIssueDescription();
    String getDetails();
    String getSeverity();
    String getIssueStatus();
    Long getWorkOrderId();
    String getOrderType();
    String getOrderStatus();
}
