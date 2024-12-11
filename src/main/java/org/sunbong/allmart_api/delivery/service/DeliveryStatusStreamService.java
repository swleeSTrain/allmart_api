package org.sunbong.allmart_api.delivery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.delivery.dto.DeliveryEventDTO;

@Service
@RequiredArgsConstructor
@Log4j2
@EnableKafkaStreams
public class DeliveryStatusStreamService {

    private final StringRedisTemplate redisTemplate;
    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private final ObjectMapper objectMapper;

    public void processDeliveryStatusStream() {
        try {
            if (streamsBuilderFactoryBean.getObject() == null) {
                log.error("StreamsBuilderFactoryBean is not initialized.");
                return;
            }

            KStream<String, String> stream = streamsBuilderFactoryBean.getObject().stream("delivery-status-topic");
            stream.map((key, value) -> parseDeliveryEventSafely(value))
                    .filter((key, value) -> value != null)
                    .peek((key, value) -> log.info("Processing delivery status update: Key={}, Value={}", key, value))
                    .foreach(this::updateRedis);
        } catch (Exception e) {
            log.error("Error initializing Kafka Stream for delivery status topic", e);
        }
    }

    private KeyValue<String, String> parseDeliveryEventSafely(String event) {
        try {
            return parseDeliveryEvent(event);
        } catch (JsonProcessingException e) {
            log.error("Error parsing delivery event: {}", event, e);
            return null;
        }
    }

    private KeyValue<String, String> parseDeliveryEvent(String event) throws JsonProcessingException {
        // JSON 데이터를 DeliveryEventDTO로 변환
        DeliveryEventDTO deliveryEvent = objectMapper.readValue(event, DeliveryEventDTO.class);
        String deliveryKey = "delivery:" + deliveryEvent.getDeliveryId();
        return KeyValue.pair(deliveryKey, deliveryEvent.getNewStatus());
    }

    private void updateRedis(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        log.info("Updated Redis: Key={}, Value={}", key, value);
    }
}
