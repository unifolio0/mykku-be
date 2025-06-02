package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.FeedComment

data class CommentPreviewResponse(
    val profileImage: String,
    val content: String,
) {
    constructor(comment: FeedComment) : this(
        profileImage = comment.member.profileImage,
        content = comment.content
    )
}
