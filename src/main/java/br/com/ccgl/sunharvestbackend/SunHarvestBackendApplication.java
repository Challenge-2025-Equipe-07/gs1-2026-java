package br.com.ccgl.sunharvestbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "br.com.ccgl.sunharvestbackend.entity")
public class SunHarvestBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SunHarvestBackendApplication.class, args);
    }

}
