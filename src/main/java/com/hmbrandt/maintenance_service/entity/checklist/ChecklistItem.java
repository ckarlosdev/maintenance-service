package com.hmbrandt.maintenance_service.entity.checklist;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checklist_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItem {
//    checklist_item_id, checklist_template_id, item_text, sort_order, is_required

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_template_id", nullable = false)
    private ChecklistTemplate checklistTemplate;

    @Column(name = "item_text", nullable = false, length = 255)
    private String itemText;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = true;

    @OneToMany(mappedBy = "checklistItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResponseOption> responseOptions = new ArrayList<>();
}
