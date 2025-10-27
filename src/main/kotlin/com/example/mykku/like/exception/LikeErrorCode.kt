package com.example.mykku.like.exception

import com.example.mykku.common.exception.DomainErrorCode
import org.springframework.http.HttpStatus

enum class LikeErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    
    LIKE_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "즐겨찾기한 게시판을 찾을 수 없습니다"),
    LIKE_BOARD_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 즐겨찾기한 게시판입니다"),

    LIKE_FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요한 피드를 찾을 수 없습니다"),
    LIKE_FEED_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 피드입니다"),

    LIKE_FEED_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요한 피드 댓글을 찾을 수 없습니다"),
    LIKE_FEED_COMMENT_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 피드 댓글입니다"),

    LIKE_DAILY_MESSAGE_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요한 하루 덕담 댓글을 찾을 수 없습니다"),
    LIKE_DAILY_MESSAGE_COMMENT_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 하루 덕담 댓글입니다")
}