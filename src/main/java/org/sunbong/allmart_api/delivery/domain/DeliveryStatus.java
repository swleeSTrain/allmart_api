package org.sunbong.allmart_api.delivery.domain;

public enum DeliveryStatus {
    PENDING,        // 배달 대기
    START, //배달시작
    IN_PROGRESS,    // 배달 진행 중
    COMPLETED       // 배달 완료
}