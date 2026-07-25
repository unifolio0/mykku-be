package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.CreateFeedCommentCommand
import com.example.mykku.feed.application.dto.SingleFeedCommentResult
import com.example.mykku.feed.application.port.input.CreateFeedCommentUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.entity.FeedComment
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.member.domain.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateFeedCommentUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedCommentRepository: FeedCommentRepository
) : CreateFeedCommentUseCase {

    override fun execute(command: CreateFeedCommentCommand, member: Member): SingleFeedCommentResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(command.feedId))

        val parentCommentId = command.parentCommentId?.let { FeedCommentId.of(it) }

        val comment = FeedComment.create(
            content = command.content,
            feedId = feed.id!!,
            memberId = member.id.value,
            parentCommentId = parentCommentId
        )

        val savedComment = feedCommentRepository.save(comment, feed.id!!, member.id.value)

        return SingleFeedCommentResult(
            id = savedComment.id!!.value,
            content = savedComment.content,
            author = CommentAuthorResult(
                memberId = member.memberId,
                nickname = member.nickname,
                profileImage = member.profileImage
            ),
            likeCount = savedComment.likeCount,
            createdAt = savedComment.createdAt
        )
    }
}
