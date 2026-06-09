package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_ALERT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID_ALERT")
    private Long id;

    @Column(name = "MESSAGE", length = 500, nullable = false)
    private String message;

    @Builder.Default
    @Column(name = "ACKNOWLEDGED", length = 1, nullable = false)
    private String acknowledged = "N";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_ALERT_SEVERITY_ID_SEVERITY", nullable = false)
    private AlertSeverity severity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_FARM_ID_FARM", nullable = false)
    private Farm farm;
}
