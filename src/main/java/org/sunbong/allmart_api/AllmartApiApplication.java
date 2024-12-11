package org.sunbong.allmart_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableJpaAuditing
@SpringBootApplication
@EnableKafkaStreams
public class AllmartApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllmartApiApplication.class, args);
    }



}
