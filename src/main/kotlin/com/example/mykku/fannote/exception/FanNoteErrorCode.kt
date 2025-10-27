package com.example.mykku.fannote.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class FanNoteErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    
    FAN_NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "덕질노트를 찾을 수 없습니다"),

    FAN_NOTE_PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "덕질노트 페이지를 찾을 수 없습니다")
}