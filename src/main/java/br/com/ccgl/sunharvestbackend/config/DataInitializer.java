package br.com.ccgl.sunharvestbackend.config;

import br.com.ccgl.sunharvestbackend.entity.AlertSeverity;
import br.com.ccgl.sunharvestbackend.repository.AlertSeverityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AlertSeverityRepository alertSeverityRepository;

    @Override
    public void run(String... args) {
        if (alertSeverityRepository.count() == 0) {
            List.of("CRITICAL", "HIGH", "MEDIUM", "LOW", "INFO")
                    .forEach(name -> alertSeverityRepository.save(new AlertSeverity(name)));
        }
    }
}
