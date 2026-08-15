package com.example.mykku.dailymessage.exception

import com.example.mykku.common.exception.BaseDomainException

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

        fun replyDepthExceeded(): DailyMessageException =
            DailyMessageException(DailyMessageErrorCode.REPLY_DEPTH_EXCEEDED)
    }
}