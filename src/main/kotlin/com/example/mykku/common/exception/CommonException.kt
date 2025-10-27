package com.example.mykku.common.exception

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