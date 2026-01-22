package com.example.mykku.feed.application.usecase

import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.application.dto.DeleteFeedCommand
import com.example.mykku.feed.application.port.input.DeleteFeedUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteFeedUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val likeFeedPort: LikeFeedPort,
    private val likeFeedCommentPort: LikeFeedCommentPort,
    private val saveFeedPort: SaveFeedPort,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val contestWinnerRepository: ContestWinnerRepository
) : DeleteFeedUseCase {

    override fun execute(command: DeleteFeedCommand, member: Member) {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(command.feedId))
        validateFeedOwner(feed, member)
        deleteRelatedData(feed)
        deleteFeed(feed)
    }

    private fun validateFeedOwner(feed: FeedJpaEntity, member: Member) {
        if (feed.member.id != member.id.value) {
            throw FeedException.feedForbiddenAccess()
        }
    }

    private fun deleteRelatedData(feed: FeedJpaEntity) {
        val commentIds = feedCommentRepository.findIdsByFeed(feed)

        likeFeedCommentPort.deleteAllByFeedCommentIdIn(commentIds)
        feedCommentRepository.deleteAllByFeed(feed)
        likeFeedPort.deleteAllByFeedId(feed.id!!)

        saveFeedPort.deleteAllByFeedId(feed.id!!)

        val participations = contestParticipationRepository.findByFeedId(feed.id!!)
        val participationIds = participations.map { it.id }
        contestWinnerRepository.deleteAllByParticipationIds(participationIds)
        contestParticipationRepository.deleteAll(participations)
    }

    private fun deleteFeed(feed: FeedJpaEntity) {
        feedTagRepository.deleteAllByFeed(feed)
        feedImageRepository.deleteAllByFeed(feed)
        feedRepository.delete(feed)
    }
}
