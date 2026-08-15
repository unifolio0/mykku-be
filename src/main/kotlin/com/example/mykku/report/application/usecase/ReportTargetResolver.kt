package com.example.mykku.report.application.usecase

import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.report.domain.vo.ReportTargetType
import com.example.mykku.report.exception.ReportException
import org.springframework.stereotype.Component

@Component
class ReportTargetResolver(
    private val feedRepository: FeedRepository,
    private val feedCommentRepository: FeedCommentRepository
) {

    fun resolveTargetMemberId(targetType: ReportTargetType, targetId: Long): Long? {
        return when (targetType) {
            ReportTargetType.FEED -> resolveFeedAuthorId(targetId)
            ReportTargetType.FEED_COMMENT -> resolveFeedCommentAuthorId(targetId)
        }
    }

    private fun resolveFeedAuthorId(targetId: Long): Long? {
        val feed = feedRepository.findById(FeedId.of(targetId)) ?: throw ReportException.targetNotFound()
        return feed.memberId
    }

    private fun resolveFeedCommentAuthorId(targetId: Long): Long? {
        val comment = feedCommentRepository.findById(FeedCommentId.of(targetId))
            ?: throw ReportException.targetNotFound()
        return comment.memberId
    }
}
