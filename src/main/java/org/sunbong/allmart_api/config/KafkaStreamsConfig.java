package org.sunbong.allmart_api.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;


@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean
    public KStream<String, String> deliveryStream(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream("delivery-events",
                Consumed.with(Serdes.String(), Serdes.String()));
        stream.foreach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
        return stream;
    }
}


