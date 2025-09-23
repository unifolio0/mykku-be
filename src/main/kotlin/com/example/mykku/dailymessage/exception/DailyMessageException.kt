package com.example.mykku.dailymessage.exception

import com.example.mykku.common.exception.BaseDomainException

/**
 * DailyMessage 도메인 예외 클래스
 */
class DailyMessageException(
    errorCode: DailyMessageErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun dailyMessageNotFound(): DailyMessageException =
            DailyMessageException(DailyMessageErrorCode.DAILY_MESSAGE_NOT_FOUND)

        fun dailyMessageContentTooLong(): DailyMessageException =
            DailyMessageException(DailyMessageErrorCode.DAILY_MESSAGE_CONTENT_TOO_LONG)

        fun dailyMessageCommentNotFound(): DailyMessageException =
            DailyMessageException(DailyMessageErrorCode.DAILY_MESSAGE_COMMENT_NOT_FOUND)

        fun commentForbiddenAccess(): DailyMessageException =
            DailyMessageException(DailyMessageErrorCode.COMMENT_FORBIDDEN_ACCESS)

        fun dailyMessageCommentContentTooLong(): DailyMessageException =
            DailyMessageException(DailyMessageErrorCode.DAILY_MESSAGE_COMMENT_CONTENT_TOO_LONG)
    }
}