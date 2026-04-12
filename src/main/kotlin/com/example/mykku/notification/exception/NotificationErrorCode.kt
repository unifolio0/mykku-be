package com.example.mykku.notification.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class NotificationErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    NOTIFICATION_NOT_FOUND("NF001", HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다"),
    FCM_TOKEN_NOT_FOUND("NF002", HttpStatus.NOT_FOUND, "FCM 토큰을 찾을 수 없습니다"),
    NOTIFICATION_SETTING_NOT_FOUND("NF003", HttpStatus.NOT_FOUND, "알림 설정을 찾을 수 없습니다"),

    FCM_TOKEN_INVALID("NF101", HttpStatus.BAD_REQUEST, "유효하지 않은 FCM 토큰입니다"),
    INVALID_NOTIFICATION_TYPE("NF102", HttpStatus.BAD_REQUEST, "유효하지 않은 알림 타입입니다"),

    NOTIFICATION_NOT_AUTHORIZED("NF201", HttpStatus.FORBIDDEN, "알림에 대한 권한이 없습니다"),

    FCM_SEND_FAILED("NF401", HttpStatus.INTERNAL_SERVER_ERROR, "FCM 알림 발송에 실패했습니다")
}
