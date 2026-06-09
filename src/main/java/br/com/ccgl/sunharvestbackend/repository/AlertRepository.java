package br.com.ccgl.sunharvestbackend.repository;

import br.com.ccgl.sunharvestbackend.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByFarmId(Long farmId);
    List<Alert> findByFarmIdAndAcknowledged(Long farmId, String acknowledged);
    List<Alert> findByFarmIdAndSeverityName(Long farmId, String severityName);
    List<Alert> findByFarmIdAndSeverityNameAndAcknowledged(Long farmId, String severityName, String acknowledged);
    boolean existsByFarmIdAndSeverityNameAndAcknowledged(Long farmId, String severityName, String acknowledged);
}
