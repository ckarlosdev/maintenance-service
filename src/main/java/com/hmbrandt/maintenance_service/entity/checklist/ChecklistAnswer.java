package com.hmbrandt.maintenance_service.entity.checklist;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "checklist_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistAnswer {
    //checklist_answer_id, checklist_id, checklist_item_id, answer, answer_comment

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_item_id", nullable = false)
    private ChecklistItem checklistItem;

    @Column(name = "answer", length = 20)
    private String answer;

    @Column(name = "answer_comment", columnDefinition = "TEXT")
    private String answerComment;
}
