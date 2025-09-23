package com.example.mykku.common.exception

/**
 * 공통 예외 클래스
 * 도메인에 속하지 않는 일반적인 예외를 처리
 */
class CommonException(
    errorCode: CommonErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun invalidSortDirection(): CommonException =
            CommonException(CommonErrorCode.INVALID_SORT_DIRECTION)

        fun invalidPageNumber(): CommonException =
            CommonException(CommonErrorCode.INVALID_PAGE_NUMBER)

        fun invalidPageSize(): CommonException =
            CommonException(CommonErrorCode.INVALID_PAGE_SIZE)
    }
}