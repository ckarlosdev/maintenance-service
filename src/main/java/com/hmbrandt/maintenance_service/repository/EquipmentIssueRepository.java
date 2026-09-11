package com.hmbrandt.maintenance_service.repository;

import com.hmbrandt.maintenance_service.dto.EquipmentIssueProjection;
import com.hmbrandt.maintenance_service.entity.EquipmentIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentIssueRepository extends JpaRepository<EquipmentIssue, Long> {
    List<EquipmentIssue> findByEquipmentId(Long equipmentId);

    @Query(value = """
        SELECT 
            E.equipment_issue_id AS id,
            E.equipment_id AS equipmentId,
            E.reference_id AS referenceId,
            E.reported_by AS reportedBy,
            E.reported_at AS reportedAt,
            E.issue_type AS issueType,
            E.issue_description AS issueDescription,
            E.details,
            E.severity AS severity,
            E.issue_status AS issueStatus,
            E.work_order_id AS workOrderId,
            W.order_type AS orderType,
            W.order_status AS orderStatus
        FROM equipment_issues E
        LEFT JOIN work_orders W 
            ON E.work_order_id = W.work_order_id 
           AND W.closed_at IS NULL 
           AND W.deleted_at IS NULL
        WHERE E.equipment_id IN (:equipmentIds)
          AND E.deleted_at IS NULL
          AND E.issue_status IN ('OPEN', 'IN_PROGRESS')
        """, nativeQuery = true)
    List<EquipmentIssueProjection> findActiveIssuesByEquipmentIds(@Param("equipmentIds") List<Long> equipmentIds);

    List<EquipmentIssue> findByIssueStatusAndDeletedAtIsNullOrderByReportedAtDesc(String status);
    List<EquipmentIssue> findByIssueStatusAndEquipmentIdAndDeletedAtIsNullOrderByReportedAtDesc(String status, Long equipmentId);

    List<EquipmentIssue> findBySeverityAndIssueStatusInAndDeletedAtIsNullOrderByReportedAtDesc(String severity, List<String> statuses);
    List<EquipmentIssue> findBySeverityAndIssueStatusInAndEquipmentIdAndDeletedAtIsNullOrderByReportedAtDesc(String severity, List<String> statuses, Long equipmentId);
}
