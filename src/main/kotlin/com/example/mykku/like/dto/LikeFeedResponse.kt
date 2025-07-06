package com.example.mykku.like.dto

import com.example.mykku.like.domain.LikeFeed

data class LikeFeedResponse(
    val id: Long,
    val memberId: String,
    val feedId: Long,
) {
    constructor(likeFeed: LikeFeed) : this(
        id = likeFeed.id!!,
        memberId = likeFeed.member.id,
        feedId = likeFeed.feed.id!!
    )
}
