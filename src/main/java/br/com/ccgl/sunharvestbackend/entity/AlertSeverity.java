package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_ALERT_SEVERITY")
@Getter
@Setter
@NoArgsConstructor
public class AlertSeverity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID_SEVERITY")
    private Long id;

    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    private String name;

    public AlertSeverity(String name) {
        this.name = name;
    }
}
