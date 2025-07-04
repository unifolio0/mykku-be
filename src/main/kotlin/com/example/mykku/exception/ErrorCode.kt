package com.example.mykku.exception

import com.example.mykku.board.domain.Board
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.domain.Tag
import com.example.mykku.member.domain.Member
import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val message: String) {
    // Daily Message
    NOT_FOUND_DAILY_MESSAGE(HttpStatus.NOT_FOUND, "하루 덕담을 찾을 수 없습니다"),
    DAILY_MESSAGE_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "하루 덕담은 ${DailyMessage.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    FORBIDDEN_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "댓글에 대한 권한이 없습니다"),

    // Feed
    FEED_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "피드 내용은 ${Feed.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
    FEED_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "피드 이미지는 ${Feed.IMAGE_MAX_COUNT}개 이하여야 합니다"),
    FEED_TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "피드 태그는 ${Feed.TAG_MAX_COUNT}개 이하여야 합니다"),

    // Tag
    TAG_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "태그는 ${Tag.TITLE_MAX_LENGTH}자 이하여야 합니다"),
    TAG_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "태그는 한글, 영문, 숫자만 사용할 수 있습니다"),

    // Board
    BOARD_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "게시판 제목은 ${Board.TITLE_MAX_LENGTH}자 이하여야 합니다"),

    // Member
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),
    MEMBER_NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST, "닉네임은 ${Member.NICKNAME_MAX_LENGTH}자 이하여야 합니다"),
    MEMBER_NICKNAME_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다"),

    // Comment
    FEED_COMMENT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "피드 댓글은 ${FeedComment.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
    DAILY_MESSAGE_COMMENT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "하루 덕담 댓글은 ${DailyMessageComment.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
}
