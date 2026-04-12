package com.example.mykku.dailymessage.exception

import com.example.mykku.common.exception.DomainErrorCode
import com.example.mykku.dailymessage.domain.entity.DailyMessage
import com.example.mykku.dailymessage.domain.entity.DailyMessageComment
import org.springframework.http.HttpStatus

enum class DailyMessageErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {

    DAILY_MESSAGE_NOT_FOUND("DM001", HttpStatus.NOT_FOUND, "일상 메시지를 찾을 수 없습니다"),
    DAILY_MESSAGE_COMMENT_NOT_FOUND("DM002", HttpStatus.NOT_FOUND, "일상 메시지 댓글을 찾을 수 없습니다"),

    DAILY_MESSAGE_CONTENT_TOO_LONG("DM101", HttpStatus.BAD_REQUEST, "일상 메시지 내용은 ${DailyMessage.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
    DAILY_MESSAGE_COMMENT_CONTENT_TOO_LONG("DM102", HttpStatus.BAD_REQUEST, "일상 메시지 댓글은 ${DailyMessageComment.CONTENT_MAX_LENGTH}자 이하여야 합니다"),

    COMMENT_FORBIDDEN_ACCESS("DM201", HttpStatus.FORBIDDEN, "댓글에 접근할 권한이 없습니다")
}
