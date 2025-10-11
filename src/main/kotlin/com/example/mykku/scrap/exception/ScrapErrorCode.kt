package com.example.mykku.scrap.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class ScrapErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다"),
    FOLDER_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 폴더 이름입니다"),
    FOLDER_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "폴더 이름은 50자 이하여야 합니다"),
    FOLDER_UNAUTHORIZED(HttpStatus.FORBIDDEN, "해당 폴더에 접근할 권한이 없습니다"),

    SAVE_FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 피드를 찾을 수 없습니다"),
    SAVE_FEED_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 저장된 피드입니다"),

    SAVE_DAILY_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 하루덕담을 찾을 수 없습니다"),
    SAVE_DAILY_MESSAGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 저장된 하루덕담입니다"),

    SAVE_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 이벤트를 찾을 수 없습니다"),
    SAVE_EVENT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 저장된 이벤트입니다"),

    SAVE_FAN_NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 덕질노트를 찾을 수 없습니다"),
    SAVE_FAN_NOTE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 저장된 덕질노트입니다")
}
