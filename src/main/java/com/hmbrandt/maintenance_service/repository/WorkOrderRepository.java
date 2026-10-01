package com.hmbrandt.maintenance_service.repository;

import com.hmbrandt.maintenance_service.dto.DashboardMetricsDTO;
import com.hmbrandt.maintenance_service.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByEquipmentId(Long equipmentId);

    @Query(value = """
        SELECT 
            (
                SELECT COUNT(*) 
                FROM work_orders 
                WHERE order_status IN ('DRAFT', 'SCHEDULED', 'OPEN')
                  AND deleted_at IS NULL
            ) AS pendingWorkOrders,

            (
                SELECT COUNT(*) 
                FROM equipment_preventive_schedules 
                WHERE (is_overdue = TRUE OR due_date <= DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY))
                  AND deleted_at IS NULL
            ) AS dueSoonMaintenances,

            (
                SELECT COUNT(*) 
                FROM equipment_issues 
                WHERE issue_status = 'IN_PROGRESS'
                  AND deleted_at IS NULL
            ) AS inProgressIssues,

            (
                SELECT COUNT(*) 
                FROM equipment_issues 
                WHERE severity = 'CRITICAL' 
                  AND issue_status IN ('OPEN', 'IN_PROGRESS')
                  AND deleted_at IS NULL
            ) AS criticalOpenIssues
        """, nativeQuery = true)
    DashboardMetricsDTO getDashboardMetrics();

    List<WorkOrder> findByOrderStatusInAndDeletedAtIsNullOrderByCreatedAtDesc(List<String> statuses);
    List<WorkOrder> findByOrderStatusInAndEquipmentIdAndDeletedAtIsNullOrderByCreatedAtDesc(List<String> statuses, Long equipmentId);
}
