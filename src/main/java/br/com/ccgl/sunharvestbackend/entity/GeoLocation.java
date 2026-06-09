package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoLocation {

    @Column(name = "LATITUDE", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 11, scale = 7)
    private BigDecimal longitude;

    @Column(name = "ALTITUDE", precision = 8, scale = 2)
    private BigDecimal altitude;
}
