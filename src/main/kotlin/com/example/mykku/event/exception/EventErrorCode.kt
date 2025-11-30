package com.example.mykku.event.exception

import com.example.mykku.common.exception.DomainErrorCode
import com.example.mykku.event.domain.Event
import org.springframework.http.HttpStatus

enum class EventErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "이벤트를 찾을 수 없습니다"),
    EVENT_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "이벤트 이미지는 ${Event.IMAGE_MAX_COUNT}개 이하여야 합니다"),
    INVALID_EVENT_STATUS(HttpStatus.BAD_REQUEST, "이벤트 상태는 'ACTIVE', 'EXPIRED', 'ALL' 중 하나여야 합니다"),
    IMAGE_INVALID_DIMENSIONS(HttpStatus.BAD_REQUEST, "이미지 크기가 유효하지 않습니다")
}
