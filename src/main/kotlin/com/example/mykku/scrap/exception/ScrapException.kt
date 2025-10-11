package com.example.mykku.scrap.exception

import com.example.mykku.common.exception.BaseDomainException

class ScrapException(
    errorCode: ScrapErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun folderNotFound(): ScrapException = ScrapException(ScrapErrorCode.FOLDER_NOT_FOUND)
        fun folderNameDuplicate(): ScrapException = ScrapException(ScrapErrorCode.FOLDER_NAME_DUPLICATE)
        fun folderNameTooLong(): ScrapException = ScrapException(ScrapErrorCode.FOLDER_NAME_TOO_LONG)
        fun folderUnauthorized(): ScrapException = ScrapException(ScrapErrorCode.FOLDER_UNAUTHORIZED)

        fun saveFeedNotFound(): ScrapException = ScrapException(ScrapErrorCode.SAVE_FEED_NOT_FOUND)
        fun saveFeedAlreadyExists(): ScrapException = ScrapException(ScrapErrorCode.SAVE_FEED_ALREADY_EXISTS)

        fun saveDailyMessageNotFound(): ScrapException = ScrapException(ScrapErrorCode.SAVE_DAILY_MESSAGE_NOT_FOUND)
        fun saveDailyMessageAlreadyExists(): ScrapException = ScrapException(ScrapErrorCode.SAVE_DAILY_MESSAGE_ALREADY_EXISTS)

        fun saveEventNotFound(): ScrapException = ScrapException(ScrapErrorCode.SAVE_EVENT_NOT_FOUND)
        fun saveEventAlreadyExists(): ScrapException = ScrapException(ScrapErrorCode.SAVE_EVENT_ALREADY_EXISTS)

        fun saveFanNoteNotFound(): ScrapException = ScrapException(ScrapErrorCode.SAVE_FAN_NOTE_NOT_FOUND)
        fun saveFanNoteAlreadyExists(): ScrapException = ScrapException(ScrapErrorCode.SAVE_FAN_NOTE_ALREADY_EXISTS)
    }
}
