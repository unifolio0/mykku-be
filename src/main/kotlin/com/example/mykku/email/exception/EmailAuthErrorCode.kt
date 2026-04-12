package com.example.mykku.email.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class EmailAuthErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    VERIFICATION_CODE_EXPIRED("EM101", HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다"),
    INVALID_VERIFICATION_CODE("EM102", HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다"),

    INVALID_EMAIL_OR_PASSWORD("EM201", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다"),

    EMAIL_ALREADY_EXISTS("EM301", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다"),

    EMAIL_SEND_FAILED("EM401", HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다"),

    TOO_MANY_REQUESTS("EM501", HttpStatus.TOO_MANY_REQUESTS, "너무 많은 요청이 발생했습니다. 잠시 후 다시 시도해주세요")
}
