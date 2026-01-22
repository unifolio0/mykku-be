package com.example.mykku.feed.application.usecase

import com.example.mykku.contest.tool.ContestParticipationReader
import com.example.mykku.contest.tool.ContestParticipationWriter
import com.example.mykku.contest.tool.ContestWinnerWriter
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.application.dto.DeleteFeedCommand
import com.example.mykku.feed.application.port.input.DeleteFeedUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.like.tool.LikeFeedCommentWriter
import com.example.mykku.like.tool.LikeFeedWriter
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.tool.SaveFeedWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteFeedUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val likeFeedWriter: LikeFeedWriter,
    private val likeFeedCommentWriter: LikeFeedCommentWriter,
    private val saveFeedWriter: SaveFeedWriter,
    private val contestParticipationReader: ContestParticipationReader,
    private val contestParticipationWriter: ContestParticipationWriter,
    private val contestWinnerWriter: ContestWinnerWriter
) : DeleteFeedUseCase {

    override fun execute(command: DeleteFeedCommand, member: Member) {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(command.feedId))
        validateFeedOwner(feed, member)
        deleteRelatedData(feed)
        deleteFeed(feed)
    }

    private fun validateFeedOwner(feed: FeedJpaEntity, member: Member) {
        if (feed.member.id != member.id) {
            throw FeedException.feedForbiddenAccess()
        }
    }

    private fun deleteRelatedData(feed: FeedJpaEntity) {
        val commentIds = feedCommentRepository.findIdsByFeed(feed)

        likeFeedCommentWriter.deleteAllByFeedCommentIds(commentIds)
        feedCommentRepository.deleteAllByFeed(feed)
        likeFeedWriter.deleteAllByFeedId(feed.id!!)

        val legacyFeed = createLegacyFeed(feed)
        saveFeedWriter.deleteAllByFeed(legacyFeed)

        val participations = contestParticipationReader.getParticipationsByFeed(legacyFeed)
        contestWinnerWriter.deleteAllByParticipations(participations)
        contestParticipationWriter.deleteAllByFeed(legacyFeed)
    }

    private fun deleteFeed(feed: FeedJpaEntity) {
        feedTagRepository.deleteAllByFeed(feed)
        feedImageRepository.deleteAllByFeed(feed)
        feedRepository.delete(feed)
    }

    private fun createLegacyFeed(feed: FeedJpaEntity): com.example.mykku.feed.domain.Feed {
        return com.example.mykku.feed.domain.Feed(
            id = feed.id,
            title = feed.title,
            content = feed.content,
            likeCount = feed.likeCount,
            commentCount = feed.commentCount,
            board = feed.board,
            member = feed.member
        )
    }
}
