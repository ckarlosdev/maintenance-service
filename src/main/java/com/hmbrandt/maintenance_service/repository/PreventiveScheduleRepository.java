package com.hmbrandt.maintenance_service.repository;

import com.hmbrandt.maintenance_service.dto.PreventiveScheduleResponseDto;
import com.hmbrandt.maintenance_service.dto.PreventiveScheduleResponseProjection;
import com.hmbrandt.maintenance_service.entity.PreventiveSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PreventiveScheduleRepository extends JpaRepository<PreventiveSchedule, Long> {
    List<PreventiveSchedule> findByEquipmentId(Long equipmentId);

    PreventiveScheduleRepository findByPreventivePlanId(Long id);

    @Query(value = """
            SELECT 
                eps.equipment_preventive_schedule_id AS equipmentPreventiveScheduleId,
                eps.preventive_plan_id AS preventivePlanId,
                eps.equipment_id AS equipmentId,
                eps.last_performed_date AS lastPerformedDate,
                eps.last_performed_meter AS lastPerformedMeter,
                eps.due_date AS dueDate,
                eps.due_meter AS dueMeter,
                eps.is_overdue AS isOverdue,
                active_wo.work_order_id AS workOrderId,
                active_wo.order_status AS orderStatus,
                active_wo.is_completed AS isCompleted,
                CASE 
                    WHEN active_wo.preventive_plan_id IS NOT NULL THEN 'assigned'
                    ELSE 'unassigned'
                END AS schedule_status 
            FROM equipment_preventive_schedules eps
            LEFT JOIN (
                SELECT 
                    wot.preventive_plan_id,
                    wo.equipment_id,
                    wo.work_order_id,
                    wo.order_status,
                    wot.is_completed
                FROM work_orders wo
                INNER JOIN work_order_tasks wot
                    ON wot.work_order_id = wo.work_order_id
                   AND wot.deleted_at IS NULL
                WHERE wo.deleted_at IS NULL
                  AND wo.order_status != 'CLOSED' 
                  AND wot.preventive_plan_id IS NOT NULL
            ) active_wo 
                ON active_wo.preventive_plan_id = eps.equipment_preventive_schedule_id
               AND active_wo.equipment_id = eps.equipment_id
            WHERE eps.equipment_id IN ( :equipmentIds )
              AND eps.deleted_at IS NULL
        """, nativeQuery = true)
    List<PreventiveScheduleResponseProjection> findActiveSchedulesByEquipmentIds(@Param("equipmentIds") List<Long> equipmentIds);

    @Query("SELECT s FROM PreventiveSchedule s " +
            "WHERE (s.isOverdue = true OR s.dueDate <= :limitDate) " +
            "AND s.deletedAt IS NULL " +
            "ORDER BY s.isOverdue DESC, s.dueDate ASC")
    List<PreventiveSchedule> findDueSoon(@Param("limitDate") LocalDate limitDate);

    @Query("SELECT s FROM PreventiveSchedule s " +
            "WHERE (s.isOverdue = true OR s.dueDate <= :limitDate) " +
            "AND s.equipmentId = :equipmentId " +
            "AND s.deletedAt IS NULL " +
            "ORDER BY s.isOverdue DESC, s.dueDate ASC")
    List<PreventiveSchedule> findDueSoonByEquipment(
            @Param("limitDate") LocalDate limitDate,
            @Param("equipmentId") Long equipmentId);

    Optional<PreventiveSchedule> findByEquipmentIdAndId(Long equipmentId, Long id);
}
