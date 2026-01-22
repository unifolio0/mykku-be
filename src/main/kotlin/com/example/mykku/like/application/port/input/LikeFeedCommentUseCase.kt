package com.example.mykku.like.application.port.input

import com.example.mykku.like.application.dto.LikeFeedCommentCommand
import com.example.mykku.like.application.dto.LikeFeedCommentResult
import com.example.mykku.like.application.dto.UnlikeFeedCommentCommand

interface LikeFeedCommentUseCase {
    fun likeFeedComment(command: LikeFeedCommentCommand): LikeFeedCommentResult
    fun unlikeFeedComment(command: UnlikeFeedCommentCommand)
    fun isLiked(memberId: String, feedCommentId: Long): Boolean
    fun deleteAllByFeedCommentIds(feedCommentIds: List<Long>)
}
