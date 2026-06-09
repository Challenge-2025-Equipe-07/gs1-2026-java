package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_FARM_SOIL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmSoil {

    @Id
    @Column(name = "ID_SOIL", length = 60)
    private String id;

    @Column(name = "SOIL_TYPE", length = 100, nullable = false)
    private String soilType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_FARM_ID_FARM", nullable = false)
    private Farm farm;

    @PrePersist
    void prePersist() {
        if (this.id == null && this.farm != null && this.farm.getId() != null) {
            this.id = "SOIL-" + this.farm.getId();
        }
    }
}
