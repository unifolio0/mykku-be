package com.example.mykku.common.exception

import org.springframework.http.HttpStatus

/**
 * 공통 에러 코드
 * 도메인에 속하지 않는 일반적인 에러들을 정의
 */
enum class CommonErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    // Validation
    INVALID_SORT_DIRECTION(HttpStatus.BAD_REQUEST, "잘못된 정렬 방향입니다. 'asc' 또는 'desc'를 사용해주세요"),
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "페이지 번호는 0 이상이어야 합니다"),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "페이지 크기는 1 이상 100 이하여야 합니다"),
    
    // General
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 관리자에게 문의해주세요.")
}