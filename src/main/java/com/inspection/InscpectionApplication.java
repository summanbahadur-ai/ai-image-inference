package com.inspection;

import com.inspection.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class InscpectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(InscpectionApplication.class, args);
    }
}
