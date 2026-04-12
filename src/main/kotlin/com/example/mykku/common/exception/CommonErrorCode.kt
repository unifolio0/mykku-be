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
    INVALID_PAGE_SIZE("C104", HttpStatus.BAD_REQUEST, "페이지 크기는 1 이상 100 이하여야 합니다"),

    REDIS_CONNECTION_FAILURE("C401", HttpStatus.INTERNAL_SERVER_ERROR, "Redis 연결에 실패했습니다. 잠시 후 다시 시도해주세요"),

    INTERNAL_SERVER_ERROR("C402", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 관리자에게 문의해주세요")
}
