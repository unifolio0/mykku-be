package com.example.mykku.dailymessage.exception

import com.example.mykku.common.exception.DomainErrorCode
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import org.springframework.http.HttpStatus

/**
 * DailyMessage 도메인 에러 코드
 */
enum class DailyMessageErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    // Daily Message
    DAILY_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "일상 메시지를 찾을 수 없습니다"),
    DAILY_MESSAGE_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "일상 메시지 내용은 ${DailyMessage.CONTENT_MAX_LENGTH}자 이하여야 합니다"),

    // Daily Message Comment
    DAILY_MESSAGE_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "일상 메시지 댓글을 찾을 수 없습니다"),
    COMMENT_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "댓글에 접근할 권한이 없습니다"),
    DAILY_MESSAGE_COMMENT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "일상 메시지 댓글은 ${DailyMessageComment.CONTENT_MAX_LENGTH}자 이하여야 합니다")
}