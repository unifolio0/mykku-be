package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.FeedCommentReplyResult
import com.example.mykku.feed.application.dto.FeedCommentResult
import com.example.mykku.feed.application.dto.FeedCommentsResult
import com.example.mykku.feed.application.dto.GetFeedCommentsQuery
import com.example.mykku.feed.application.port.input.GetFeedCommentsUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFeedCommentsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val memberRepository: MemberRepository,
    private val likeFeedCommentPort: LikeFeedCommentPort
) : GetFeedCommentsUseCase {

    override fun execute(query: GetFeedCommentsQuery): FeedCommentsResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(query.feedId))
        val commentsPage = feedCommentRepository.findByFeedIdAndParentCommentIsNull(feed.id!!, query.pageable)

        val parentCommentIds = commentsPage.content.mapNotNull { it.id }
        val repliesMap = if (parentCommentIds.isNotEmpty()) {
            feedCommentRepository.findByParentCommentIds(parentCommentIds)
                .groupBy { it.parentCommentId?.value ?: 0L }
        } else {
            emptyMap()
        }

        val allComments = commentsPage.content + repliesMap.values.flatten()
        val memberIds = allComments.mapNotNull { it.memberId }.distinct()
        val membersMap = memberIds.mapNotNull { memberRepository.findById(MemberPk.of(it)) }
            .associateBy { it.id.value }

        val commentResponses = commentsPage.content.map { comment ->
            val replies = repliesMap[comment.id?.value] ?: emptyList()
            val commentMember = comment.memberId?.let { membersMap[it] }

            val replyResponses = replies.map { reply ->
                val replyMember = reply.memberId?.let { membersMap[it] }
                val replyAuthor = replyMember?.let {
                    CommentAuthorResult(
                        memberId = it.memberId,
                        nickname = it.nickname,
                        profileImage = it.profileImage
                    )
                }
                FeedCommentReplyResult(
                    id = reply.id!!.value,
                    content = reply.content,
                    author = replyAuthor,
                    likeCount = reply.likeCount,
                    isLiked = query.memberId?.let { likeFeedCommentPort.existsByMemberIdAndFeedCommentId(it, reply.id!!.value) } ?: false,
                    createdAt = reply.createdAt,
                    updatedAt = reply.updatedAt
                )
            }

            val commentAuthor = commentMember?.let {
                CommentAuthorResult(
                    memberId = it.memberId,
                    nickname = it.nickname,
                    profileImage = it.profileImage
                )
            }

            FeedCommentResult(
                id = comment.id!!.value,
                content = comment.content,
                author = commentAuthor,
                likeCount = comment.likeCount,
                isLiked = query.memberId?.let { likeFeedCommentPort.existsByMemberIdAndFeedCommentId(it, comment.id!!.value) } ?: false,
                replies = replyResponses,
                replyCount = replyResponses.size,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }

        return FeedCommentsResult(
            comments = commentResponses,
            totalElements = commentsPage.totalElements,
            totalPages = commentsPage.totalPages,
            currentPage = commentsPage.number,
            pageSize = commentsPage.size,
            hasNext = commentsPage.hasNext()
        )
    }
}
