package com.example.mykku.event.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class EventErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "이벤트를 찾을 수 없습니다"),
    EVENT_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "이벤트 이미지는 최대 10개까지 등록할 수 있습니다")
}
