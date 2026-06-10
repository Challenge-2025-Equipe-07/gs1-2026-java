package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_FARM_SOLAR_PANEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmSolarPanel {

    @Id
    @Column(name = "ID_SOLAR_PANEL", length = 20)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_FARM_ID_FARM", nullable = false)
    private Farm farm;

    @Column(name = "SOLAR_PANEL_CAPACITY", precision = 10, scale = 2)
    private BigDecimal solarPanelCapacity;

    @Column(name = "PUMP_POWER", precision = 10, scale = 2)
    private BigDecimal pumpPower;

    @Column(name = "TILT_DEGREES", precision = 5, scale = 2)
    private BigDecimal tiltDegrees;

    @Column(name = "AZIMUTH_DEGREES", precision = 6, scale = 2)
    private BigDecimal azimuthDegrees;

    @Column(name = "PERFORMANCE_RATIO", precision = 4, scale = 3)
    private BigDecimal performanceRatio;

    @PrePersist
    void prePersist() {
        if (this.id == null && this.farm != null && this.farm.getId() != null) {
            this.id = "SP-" + this.farm.getId();
        }
    }
}
