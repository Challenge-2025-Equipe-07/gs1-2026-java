package br.com.ccgl.sunharvestbackend;

import br.com.ccgl.sunharvestbackend.repository.SimpleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = SimpleRepository.class)
@EntityScan(basePackages = "br.com.ccgl.sunharvestbackend.entity")
public class SunHarvestBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SunHarvestBackendApplication.class, args);
    }

}
