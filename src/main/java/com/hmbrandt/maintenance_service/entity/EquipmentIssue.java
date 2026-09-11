package com.hmbrandt.maintenance_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE equipment_issues SET deleted_at = NOW() WHERE equipment_issue_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class EquipmentIssue {
    //equipment_issue_id, equipment_id, reported_by, reported_at, issue_description, severity, issue_status, work_order_id, parent_issue_id

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_issue_id")
    private Long id;

    @Column(name = "equipment_id", nullable = false)
    private Long equipmentId;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reported_by", nullable = false)
    private String reportedBy;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Column(name = "issue_type")
    private String issueType;

    @Column(name = "issue_description", nullable = false)
    private String issueDescription;

    @Column(name = "details")
    private String details;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "issue_status", nullable = false)
    private String issueStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    @Column(name = "parent_issue_id")
    private Long parentIssueId;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
