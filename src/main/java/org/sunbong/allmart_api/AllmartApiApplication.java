package org.sunbong.allmart_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AllmartApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllmartApiApplication.class, args);
    }



}
