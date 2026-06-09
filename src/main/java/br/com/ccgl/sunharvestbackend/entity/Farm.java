package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TB_FARM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID_FARM")
    private Long id;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Embedded
    private GeoLocation location;

    @Column(name = "AREA_HECTARES", precision = 10, scale = 4, nullable = false)
    private BigDecimal areaHectares;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_USER_ID_USER", nullable = false)
    private User user;

    @OneToOne(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FarmCrop crop;

    @OneToOne(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FarmSoil soil;

    @OneToOne(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FarmIrrigation irrigation;

    @Builder.Default
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FarmSolarPanel> solarPanels = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "farm", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Alert> alerts = new ArrayList<>();

    @PreUpdate
    void preUpdate() {
        this.updatedAt = new Date();
    }
}
