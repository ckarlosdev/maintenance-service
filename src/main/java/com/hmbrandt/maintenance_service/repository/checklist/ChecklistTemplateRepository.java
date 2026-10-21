package com.hmbrandt.maintenance_service.repository.checklist;

import com.hmbrandt.maintenance_service.entity.checklist.ChecklistTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistTemplateRepository extends JpaRepository<ChecklistTemplate, Long> {
}
