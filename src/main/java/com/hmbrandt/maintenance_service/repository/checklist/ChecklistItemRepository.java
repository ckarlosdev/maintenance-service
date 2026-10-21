package com.hmbrandt.maintenance_service.repository.checklist;

import com.hmbrandt.maintenance_service.entity.checklist.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
}
