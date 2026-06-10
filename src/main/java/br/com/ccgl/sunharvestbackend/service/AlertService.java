package br.com.ccgl.sunharvestbackend.service;

import br.com.ccgl.sunharvestbackend.domain.AlertRequest;
import br.com.ccgl.sunharvestbackend.domain.AlertResponse;
import br.com.ccgl.sunharvestbackend.entity.Alert;
import br.com.ccgl.sunharvestbackend.entity.AlertSeverity;
import br.com.ccgl.sunharvestbackend.entity.Farm;
import br.com.ccgl.sunharvestbackend.exception.AlertDeletionException;
import br.com.ccgl.sunharvestbackend.exception.ResourceNotFoundException;
import br.com.ccgl.sunharvestbackend.repository.AlertRepository;
import br.com.ccgl.sunharvestbackend.repository.AlertSeverityRepository;
import br.com.ccgl.sunharvestbackend.repository.FarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertSeverityRepository alertSeverityRepository;
    private final FarmRepository farmRepository;

    @Transactional
    public AlertResponse createAlert(Long farmId, AlertRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Fazenda não encontrada: " + farmId));
        AlertSeverity severity = alertSeverityRepository.findById(request.severityId())
                .orElseThrow(() -> new ResourceNotFoundException("Severidade não encontrada: " + request.severityId()));

        Alert alert = Alert.builder()
                .message(request.message())
                .severity(severity)
                .farm(farm)
                .build();

        return toAlertResponse(alertRepository.save(alert));
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByFarm(Long farmId, String severity, String acknowledged) {
        List<Alert> alerts;
        if (severity != null && acknowledged != null) {
            alerts = alertRepository.findByFarmIdAndSeverityNameAndAcknowledged(
                    farmId, severity.toUpperCase(), acknowledged.toUpperCase());
        } else if (severity != null) {
            alerts = alertRepository.findByFarmIdAndSeverityName(farmId, severity.toUpperCase());
        } else if (acknowledged != null) {
            alerts = alertRepository.findByFarmIdAndAcknowledged(farmId, acknowledged.toUpperCase());
        } else {
            alerts = alertRepository.findByFarmId(farmId);
        }
        return alerts.stream().map(this::toAlertResponse).toList();
    }

    @Transactional(readOnly = true)
    public AlertResponse getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado: " + id));
        return toAlertResponse(alert);
    }

    @Transactional
    public AlertResponse acknowledgeAlert(Long id, boolean acknowledge) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado: " + id));
        alert.setAcknowledged(acknowledge ? "Y" : "N");
        return toAlertResponse(alertRepository.save(alert));
    }

    @Transactional
    public void deleteAlert(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado: " + id));
        if ("N".equals(alert.getAcknowledged())) {
            throw new AlertDeletionException("Alerta pendente não pode ser deletado. Reconheça-o primeiro.");
        }
        alertRepository.delete(alert);
    }

    private AlertResponse toAlertResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getMessage(),
                alert.getAcknowledged(),
                alert.getSeverity() != null ? alert.getSeverity().getName() : null,
                alert.getFarm() != null ? alert.getFarm().getId() : null,
                alert.getCreatedAt());
    }
}
