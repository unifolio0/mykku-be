package com.example.mykku.fannote.exception

import com.example.mykku.common.exception.BaseDomainException

/**
 * FanNote 도메인 예외 클래스
 */
class FanNoteException(
    errorCode: FanNoteErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {

    companion object {
        fun fanNoteNotFound(): FanNoteException =
            FanNoteException(FanNoteErrorCode.FAN_NOTE_NOT_FOUND)

        fun fanNotePageNotFound(): FanNoteException =
            FanNoteException(FanNoteErrorCode.FAN_NOTE_PAGE_NOT_FOUND)
    }
}