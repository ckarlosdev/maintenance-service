package com.hmbrandt.maintenance_service.entity.service;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vehicle_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_type_id")
    private Long id;

    @Column(name = "type_name", nullable = false, length = 80)
    private String typeName;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relación opcional hacia los intervalos de servicio
    @OneToMany(mappedBy = "vehicleType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceInterval> serviceIntervals;
}
