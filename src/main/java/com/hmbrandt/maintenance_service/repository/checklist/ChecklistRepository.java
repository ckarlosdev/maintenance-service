package com.hmbrandt.maintenance_service.repository.checklist;

import com.hmbrandt.maintenance_service.entity.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    List<Checklist> findByVehicleIdOrderByCreatedDateDesc(Long vehicleId);
}
