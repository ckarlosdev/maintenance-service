package com.hmbrandt.maintenance_service.repository;

import com.hmbrandt.maintenance_service.entity.WorkOrderTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderTaskRepository extends JpaRepository<WorkOrderTask, Long> {
}
