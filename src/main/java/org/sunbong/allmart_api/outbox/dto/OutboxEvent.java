package org.sunbong.allmart_api.outbox.dto;

import lombok.Data;

@Data
public class OutboxEvent {
    private String eventType;
    private String payload;
    private boolean processed;
}