package com.example.mykku.common.exception

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    INVALID_INPUT("C101", HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    INVALID_SORT_DIRECTION("C102", HttpStatus.BAD_REQUEST, "잘못된 정렬 방향입니다. 'asc' 또는 'desc'를 사용해주세요"),
    INVALID_PAGE_NUMBER("C103", HttpStatus.BAD_REQUEST, "페이지 번호는 0 이상이어야 합니다"),
    INVALID_PAGE_SIZE("C104", HttpStatus.BAD_REQUEST, "페이지 크기는 1 이상 1000 이하여야 합니다"),
    MISSING_REQUEST_PARAMETER("C105", HttpStatus.BAD_REQUEST, "필수 요청 값이 누락되었습니다"),
    INVALID_PARAMETER_TYPE("C106", HttpStatus.BAD_REQUEST, "요청 값의 형식이 올바르지 않습니다"),

    ENDPOINT_NOT_FOUND("C201", HttpStatus.NOT_FOUND, "요청한 API를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED("C202", HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다"),
    UNSUPPORTED_MEDIA_TYPE("C203", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Content-Type입니다"),

    RESOURCE_LOCK_CONFLICT("C301", HttpStatus.CONFLICT, "다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요"),

    REDIS_CONNECTION_FAILURE("C401", HttpStatus.INTERNAL_SERVER_ERROR, "Redis 연결에 실패했습니다. 잠시 후 다시 시도해주세요"),

    INTERNAL_SERVER_ERROR("C402", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 관리자에게 문의해주세요")
}
