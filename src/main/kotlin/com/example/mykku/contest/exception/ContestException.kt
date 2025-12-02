package com.example.mykku.contest.exception

import com.example.mykku.common.exception.BaseDomainException

class ContestException(
    errorCode: ContestErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun contestNotFound(): ContestException = ContestException(ContestErrorCode.CONTEST_NOT_FOUND)

        fun contestImageLimitExceeded(): ContestException = ContestException(ContestErrorCode.CONTEST_IMAGE_LIMIT_EXCEEDED)

        fun contestTagLimitExceeded(): ContestException = ContestException(ContestErrorCode.CONTEST_TAG_LIMIT_EXCEEDED)

        fun tagTitleTooLong(): ContestException = ContestException(ContestErrorCode.TAG_TITLE_TOO_LONG)

        fun tagInvalidFormat(): ContestException = ContestException(ContestErrorCode.TAG_INVALID_FORMAT)

        fun invalidContestStatus(): ContestException = ContestException(ContestErrorCode.INVALID_CONTEST_STATUS)

        fun contestWinnerNotFound(): ContestException = ContestException(ContestErrorCode.CONTEST_WINNER_NOT_FOUND)

        fun alreadyParticipatedWithFeed(): ContestException = ContestException(ContestErrorCode.ALREADY_PARTICIPATED_WITH_FEED)
    }
}
