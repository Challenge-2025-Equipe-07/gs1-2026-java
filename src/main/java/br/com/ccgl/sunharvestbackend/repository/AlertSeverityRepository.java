package br.com.ccgl.sunharvestbackend.repository;

import br.com.ccgl.sunharvestbackend.entity.AlertSeverity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertSeverityRepository extends JpaRepository<AlertSeverity, Long> {
    Optional<AlertSeverity> findByName(String name);
}
