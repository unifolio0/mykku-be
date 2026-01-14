package com.example.mykku.block.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class BlockErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    MEMBER_BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "차단 정보를 찾을 수 없습니다"),
    MEMBER_ALREADY_BLOCKED(HttpStatus.CONFLICT, "이미 차단된 사용자입니다"),
    CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수 없습니다"),
    MEMBER_TO_BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "차단할 사용자를 찾을 수 없습니다"),

    KEYWORD_BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "키워드 차단 정보를 찾을 수 없습니다"),
    KEYWORD_ALREADY_BLOCKED(HttpStatus.CONFLICT, "이미 차단된 키워드입니다"),
    KEYWORD_TOO_LONG(HttpStatus.BAD_REQUEST, "키워드는 50자 이하여야 합니다"),
    KEYWORD_EMPTY(HttpStatus.BAD_REQUEST, "키워드를 입력해주세요"),
    KEYWORD_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "키워드는 최대 100개까지 등록할 수 있습니다")
}
