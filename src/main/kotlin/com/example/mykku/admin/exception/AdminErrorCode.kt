package com.example.mykku.admin.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class AdminErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    INVALID_TOKEN("AD201", HttpStatus.UNAUTHORIZED, "유효하지 않은 관리자 토큰입니다"),
    UNAUTHORIZED("AD202", HttpStatus.UNAUTHORIZED, "관리자 인증이 필요합니다"),

    INVALID_REQUEST("AD101", HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    MISSING_REQUIRED_FIELD("AD102", HttpStatus.BAD_REQUEST, "필수 필드가 누락되었습니다"),

    RESOURCE_NOT_FOUND("AD001", HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다"),

    INTERNAL_ERROR("AD401", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
}
