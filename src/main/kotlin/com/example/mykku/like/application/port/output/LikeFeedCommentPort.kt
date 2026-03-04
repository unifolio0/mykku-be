package com.example.mykku.like.application.port.output

import com.example.mykku.like.domain.entity.LikeFeedCommentEntity

interface LikeFeedCommentPort {
    fun save(likeFeedComment: LikeFeedCommentEntity): LikeFeedCommentEntity
    fun existsByMemberIdAndFeedCommentId(memberId: Long, feedCommentId: Long): Boolean
    fun deleteByMemberIdAndFeedCommentId(memberId: Long, feedCommentId: Long)
    fun deleteAllByFeedCommentIdIn(feedCommentIds: List<Long>)
}
