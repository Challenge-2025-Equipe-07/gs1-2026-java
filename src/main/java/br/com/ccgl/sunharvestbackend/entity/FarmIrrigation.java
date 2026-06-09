package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_FARM_IRRIGATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmIrrigation {

    @Id
    @Column(name = "ID_IRRIGATION", length = 60)
    private String id;

    @Column(name = "IRRIGATION_EFFICIENCY", precision = 4, scale = 2, nullable = false)
    private BigDecimal irrigationEfficiency;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_FARM_ID_FARM", nullable = false)
    private Farm farm;

    @PrePersist
    void prePersist() {
        if (this.id == null && this.farm != null && this.farm.getId() != null) {
            this.id = "IRR-" + this.farm.getId();
        }
    }
}
