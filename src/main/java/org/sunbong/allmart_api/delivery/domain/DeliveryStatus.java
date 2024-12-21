package org.sunbong.allmart_api.delivery.domain;

import lombok.extern.log4j.Log4j2;

@Log4j2
public enum DeliveryStatus {
    PENDING,        // 배달 대기
    START,          // 배달 시작
    IN_PROGRESS,    // 배달 진행 중
    CANCELLED,
    COMPLETED;      // 배달 완료

}
