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
                    equipment_preventive_schedule_id AS equipmentPreventiveScheduleId,
                    preventive_plan_id AS preventivePlanId,
                    equipment_id AS equipmentId,
                    last_performed_date AS lastPerformedDate,
                    last_performed_meter AS lastPerformedMeter,
                    due_date AS dueDate,
                    due_meter AS dueMeter,
                    is_overdue AS isOverdue,
                    work_order_id AS workOrderId,
                    order_status AS orderStatus,
                    is_completed AS isCompleted,
                    schedule_status AS scheduleStatus 
                FROM (
                    SELECT
                        eps.equipment_preventive_schedule_id,
                        eps.preventive_plan_id,
                        eps.equipment_id,
                        eps.last_performed_date,
                        eps.last_performed_meter,
                        eps.due_date,
                        eps.due_meter,
                        eps.is_overdue,
                        active_wo.work_order_id,
                        active_wo.order_status,
                        active_wo.is_completed,
                        CASE 
                            WHEN active_wo.preventive_plan_id IS NOT NULL THEN 'assigned'
                            ELSE 'unassigned'
                        END AS schedule_status,
            
                        ROW_NUMBER() OVER (
                            PARTITION BY eps.equipment_id
                            ORDER BY
                                (CASE WHEN active_wo.work_order_id IS NOT NULL THEN 0 ELSE 1 END) ASC,
                                eps.is_overdue DESC,
                                eps.due_date ASC,
                                eps.due_meter ASC
                        ) AS rn
            
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
                ) sub
                WHERE sub.rn = 1;
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
