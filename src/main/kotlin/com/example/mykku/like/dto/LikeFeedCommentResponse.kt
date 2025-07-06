package com.example.mykku.like.dto

import com.example.mykku.like.domain.LikeFeedComment

data class LikeFeedCommentResponse(
    val id: Long,
    val memberId: String,
    val feedCommentId: Long,
) {
    constructor(likeFeedComment: LikeFeedComment) : this(
        id = likeFeedComment.id!!,
        memberId = likeFeedComment.member.id,
        feedCommentId = likeFeedComment.feedComment.id!!
    )
}
