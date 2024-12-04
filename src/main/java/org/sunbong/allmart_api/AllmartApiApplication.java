package org.sunbong.allmart_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class AllmartApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllmartApiApplication.class, args);
    }



}
