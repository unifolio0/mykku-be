package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.SingleFeedCommentResult
import com.example.mykku.feed.application.dto.UpdateFeedCommentCommand
import com.example.mykku.feed.application.port.input.UpdateFeedCommentUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.domain.vo.FeedCommentId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.member.domain.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateFeedCommentUseCaseImpl(
    private val feedCommentRepository: FeedCommentRepository
) : UpdateFeedCommentUseCase {

    override fun execute(command: UpdateFeedCommentCommand, member: Member): SingleFeedCommentResult {
        val comment = feedCommentRepository.findByIdOrThrow(FeedCommentId.of(command.commentId))

        if (comment.member.id != member.id.value) {
            throw FeedException.feedCommentForbiddenAccess()
        }

        comment.updateContent(command.content)
        val updatedComment = feedCommentRepository.save(comment)

        return SingleFeedCommentResult(
            id = updatedComment.id!!,
            content = updatedComment.content,
            author = CommentAuthorResult(
                memberId = updatedComment.member.memberId,
                nickname = updatedComment.member.nickname,
                profileImage = updatedComment.member.profileImage
            ),
            likeCount = updatedComment.likeCount,
            createdAt = updatedComment.createdAt
        )
    }
}
