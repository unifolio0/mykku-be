package com.example.mykku.like.application.port.output

import com.example.mykku.like.domain.entity.LikeFeedCommentEntity

interface LikeFeedCommentPort {
    fun save(likeFeedComment: LikeFeedCommentEntity): LikeFeedCommentEntity
    fun existsByMemberIdAndFeedCommentId(memberId: String, feedCommentId: Long): Boolean
    fun deleteByMemberIdAndFeedCommentId(memberId: String, feedCommentId: Long)
    fun deleteAllByFeedCommentIdIn(feedCommentIds: List<Long>)
}
