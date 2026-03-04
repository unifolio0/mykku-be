package com.example.mykku.like.adapter.input.web.dto

import com.example.mykku.like.application.dto.LikeBoardInfoResult
import com.example.mykku.like.application.dto.LikeBoardResult
import com.example.mykku.like.application.dto.LikeDailyMessageCommentResult
import com.example.mykku.like.application.dto.LikeFeedCommentResult
import com.example.mykku.like.application.dto.LikeFeedResult

data class LikeFeedWebResponse(
    val id: Long,
    val memberId: Long,
    val feedId: Long
) {
    companion object {
        fun from(result: LikeFeedResult): LikeFeedWebResponse {
            return LikeFeedWebResponse(
                id = result.id,
                memberId = result.memberId,
                feedId = result.feedId
            )
        }
    }
}

data class LikeFeedCommentWebResponse(
    val id: Long,
    val memberId: Long,
    val feedCommentId: Long
) {
    companion object {
        fun from(result: LikeFeedCommentResult): LikeFeedCommentWebResponse {
            return LikeFeedCommentWebResponse(
                id = result.id,
                memberId = result.memberId,
                feedCommentId = result.feedCommentId
            )
        }
    }
}

data class LikeBoardWebResponse(
    val id: Long,
    val memberId: Long,
    val boardId: Long
) {
    companion object {
        fun from(result: LikeBoardResult): LikeBoardWebResponse {
            return LikeBoardWebResponse(
                id = result.id,
                memberId = result.memberId,
                boardId = result.boardId
            )
        }
    }
}

data class LikeBoardInfoWebResponse(
    val id: Long,
    val title: String,
    val logo: String
) {
    companion object {
        fun from(result: LikeBoardInfoResult): LikeBoardInfoWebResponse {
            return LikeBoardInfoWebResponse(
                id = result.id,
                title = result.title,
                logo = result.logo
            )
        }
    }
}

data class LikeDailyMessageCommentWebResponse(
    val id: Long,
    val memberId: Long,
    val dailyMessageCommentId: Long
) {
    companion object {
        fun from(result: LikeDailyMessageCommentResult): LikeDailyMessageCommentWebResponse {
            return LikeDailyMessageCommentWebResponse(
                id = result.id,
                memberId = result.memberId,
                dailyMessageCommentId = result.dailyMessageCommentId
            )
        }
    }
}
