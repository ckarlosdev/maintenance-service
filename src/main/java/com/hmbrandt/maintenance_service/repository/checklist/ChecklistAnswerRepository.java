package com.hmbrandt.maintenance_service.repository.checklist;

import com.hmbrandt.maintenance_service.entity.checklist.ChecklistAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistAnswerRepository extends JpaRepository<ChecklistAnswer, Long> {
}
