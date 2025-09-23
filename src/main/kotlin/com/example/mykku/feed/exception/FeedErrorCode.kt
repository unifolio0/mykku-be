package com.example.mykku.feed.exception

import com.example.mykku.common.exception.DomainErrorCode
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventTag
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import org.springframework.http.HttpStatus

/**
 * Feed 도메인 에러 코드
 */
enum class FeedErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    // Feed
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "피드를 찾을 수 없습니다"),
    FEED_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "피드 내용은 ${Feed.CONTENT_MAX_LENGTH}자 이하여야 합니다"),
    FEED_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "피드 이미지는 ${Feed.IMAGE_MAX_COUNT}개 이하여야 합니다"),
    FEED_TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "피드 태그는 ${Feed.TAG_MAX_COUNT}개 이하여야 합니다"),

    // Feed Comment
    FEED_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "피드 댓글을 찾을 수 없습니다"),
    FEED_COMMENT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "피드 댓글은 ${FeedComment.CONTENT_MAX_LENGTH}자 이하여야 합니다"),

    // Event
    EVENT_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "이벤트 이미지는 ${Event.IMAGE_MAX_COUNT}개 이하여야 합니다"),
    EVENT_TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "이벤트 태그는 ${Event.TAG_MAX_COUNT}개 이하여야 합니다"),

    // Tag
    TAG_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "태그는 ${EventTag.TITLE_MAX_LENGTH}자 이하여야 합니다"),
    TAG_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "태그는 한글, 영문, 숫자만 사용할 수 있습니다"),

    // Image validation
    IMAGE_INVALID_DIMENSIONS(HttpStatus.BAD_REQUEST, "이미지 크기가 유효하지 않습니다")
}
