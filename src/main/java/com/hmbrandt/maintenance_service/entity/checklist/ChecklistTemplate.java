package com.hmbrandt.maintenance_service.entity.checklist;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checklist_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistTemplate {
    // checklist_template_id, title, template_description, is_active, created_at

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_template_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "template_description", nullable = false, columnDefinition = "TEXT")
    private String templateDescription;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "checklistTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistItem> items = new ArrayList<>();

}
