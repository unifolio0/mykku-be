package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.adapter.output.persistence.entity.FeedCommentJpaEntity
import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.CreateFeedCommentCommand
import com.example.mykku.feed.application.dto.SingleFeedCommentResult
import com.example.mykku.feed.application.port.input.CreateFeedCommentUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.member.domain.Member
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

        val parentComment = command.parentCommentId?.let { parentId ->
            feedCommentRepository.findByIdOrThrow(FeedCommentId.of(parentId))
        }

        val comment = FeedCommentJpaEntity(
            content = command.content,
            feed = feed,
            member = member,
            parentComment = parentComment
        )

        val savedComment = feedCommentRepository.save(comment)

        return SingleFeedCommentResult(
            id = savedComment.id!!,
            content = savedComment.content,
            author = CommentAuthorResult(
                memberId = savedComment.member.memberId,
                nickname = savedComment.member.nickname,
                profileImage = savedComment.member.profileImage
            ),
            likeCount = savedComment.likeCount,
            createdAt = savedComment.createdAt
        )
    }
}
