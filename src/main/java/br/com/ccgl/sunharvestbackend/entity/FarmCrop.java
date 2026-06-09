package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_FARM_CROP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmCrop {

    @Id
    @Column(name = "ID_CROP", length = 60)
    private String id;

    @Column(name = "CROP_TYPE", length = 100, nullable = false)
    private String cropType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_FARM_ID_FARM", nullable = false)
    private Farm farm;

    @PrePersist
    void prePersist() {
        if (this.id == null && this.farm != null && this.farm.getId() != null) {
            this.id = "CROP-" + this.farm.getId();
        }
    }
}
