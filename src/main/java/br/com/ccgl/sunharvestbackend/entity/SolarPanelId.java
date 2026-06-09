package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SolarPanelId implements Serializable {

    @Column(name = "ID_SOLAR_PANEL", length = 50)
    private String idSolarPanel;

    @Column(name = "TB_FARM_ID_FARM")
    private Long farmId;
}
