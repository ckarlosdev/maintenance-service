package com.hmbrandt.maintenance_service.entity.checklist;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checklists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checklist {
    //checklist_id, checklist_template_id, vehicle_id, checklist_status, general_comment, completed_at, created_date, udpated_date

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_template_id", nullable = false)
    private ChecklistTemplate checklistTemplate;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "checklist_status", length = 20)
    @Builder.Default
    private String checklistStatus = "in progress";

    @Column(name = "general_comment", columnDefinition = "TEXT")
    private String generalComment;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_date", updatable = false, insertable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistAnswer> answers = new ArrayList<>();
}
