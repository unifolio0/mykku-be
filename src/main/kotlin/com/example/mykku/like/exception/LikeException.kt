package com.example.mykku.like.exception

import com.example.mykku.common.exception.BaseDomainException

class LikeException(
    errorCode: LikeErrorCode,
    additionalMessage: String? = null,
    cause: Throwable? = null
) : BaseDomainException(errorCode, additionalMessage, cause) {
    
    companion object {
        
        fun likeBoardNotFound(): LikeException = LikeException(LikeErrorCode.LIKE_BOARD_NOT_FOUND)
        fun likeBoardAlreadyLiked(): LikeException = LikeException(LikeErrorCode.LIKE_BOARD_ALREADY_LIKED)

        fun likeFeedNotFound(): LikeException = LikeException(LikeErrorCode.LIKE_FEED_NOT_FOUND)
        fun likeFeedAlreadyLiked(): LikeException = LikeException(LikeErrorCode.LIKE_FEED_ALREADY_LIKED)

        fun likeFeedCommentNotFound(): LikeException = LikeException(LikeErrorCode.LIKE_FEED_COMMENT_NOT_FOUND)
        fun likeFeedCommentAlreadyLiked(): LikeException = LikeException(LikeErrorCode.LIKE_FEED_COMMENT_ALREADY_LIKED)

        fun likeDailyMessageCommentNotFound(): LikeException = LikeException(LikeErrorCode.LIKE_DAILY_MESSAGE_COMMENT_NOT_FOUND)
        fun likeDailyMessageCommentAlreadyLiked(): LikeException = LikeException(LikeErrorCode.LIKE_DAILY_MESSAGE_COMMENT_ALREADY_LIKED)
    }
}