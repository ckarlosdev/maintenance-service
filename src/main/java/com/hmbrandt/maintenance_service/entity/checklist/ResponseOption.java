package com.hmbrandt.maintenance_service.entity.checklist;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "response_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseOption {
    //response_option_id, checklist_item_id, option_label, sort_order
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_item_id", nullable = false)
    private ChecklistItem checklistItem;

    @Column(name = "option_label", nullable = false, length = 100)
    private String optionLabel;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

}
