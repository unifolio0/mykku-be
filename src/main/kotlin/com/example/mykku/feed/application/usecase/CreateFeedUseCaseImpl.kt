package com.example.mykku.feed.application.usecase

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.board.application.port.output.BoardRepository
import com.example.mykku.board.domain.vo.BoardId
import com.example.mykku.board.exception.BoardException
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestTagRepository
import com.example.mykku.contest.domain.entity.ContestParticipation
import com.example.mykku.contest.domain.entity.ContestTag
import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestStatusType
import com.example.mykku.feed.application.dto.CreateFeedCommand
import com.example.mykku.feed.application.dto.CreateFeedResult
import com.example.mykku.feed.application.dto.FeedImageResult
import com.example.mykku.feed.application.port.input.CreateFeedUseCase
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.domain.entity.Feed
import com.example.mykku.feed.domain.entity.FeedImage
import com.example.mykku.feed.domain.entity.FeedTag
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.member.domain.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CreateFeedUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val boardRepository: BoardRepository,
    private val imageUploadService: ImageUploadService,
    private val contestRepository: ContestRepository,
    private val contestTagRepository: ContestTagRepository,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val activityEventPublisher: ActivityEventPublisher
) : CreateFeedUseCase {

    override fun execute(command: CreateFeedCommand, member: Member): CreateFeedResult {
        member.requireProfileCompleted()

        val board = boardRepository.findById(BoardId(command.boardId))
            ?: throw BoardException.boardNotFound()

        val imageResults = uploadImages(command.images)
        validateImageCount(imageResults.size)

        val normalizedTags = normalizeTags(command.tags)
        validateTagCount(normalizedTags.size)

        val savedFeed = saveFeed(command, member)
        val savedImages = feedImageRepository.saveAll(createFeedImages(imageResults, savedFeed), savedFeed.id!!)
        val savedTags = feedTagRepository.saveAll(createFeedTags(normalizedTags, savedFeed), savedFeed.id!!)

        recordContestParticipationIfApplicable(member, savedTags, savedFeed)

        return buildCreateFeedResult(savedFeed, savedImages, savedTags, member, board.title)
    }

    private fun saveFeed(command: CreateFeedCommand, member: Member): Feed {
        val feed = Feed.create(
            title = command.title,
            content = command.content,
            boardId = command.boardId,
            memberId = member.id.value
        )
        val savedFeed = feedRepository.save(feed, command.boardId, member.id.value)

        activityEventPublisher.publish(ActivityEvent(member.id.value, ActivityType.FEED_UPLOAD))

        return savedFeed
    }

    private fun uploadImages(images: List<org.springframework.web.multipart.MultipartFile>): List<ImageUploadResult> {
        return if (images.isEmpty()) emptyList()
        else imageUploadService.uploadImages(images, "feed-images")
    }

    private fun validateImageCount(count: Int) {
        if (count > Feed.IMAGE_MAX_COUNT) {
            throw FeedException.feedImageLimitExceeded()
        }
    }

    private fun normalizeTags(tags: List<String>): List<String> {
        return tags.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()
    }

    private fun validateTagCount(count: Int) {
        if (count > Feed.TAG_MAX_COUNT) {
            throw FeedException.feedTagLimitExceeded()
        }
    }

    private fun createFeedImages(
        imageResults: List<ImageUploadResult>,
        feed: Feed
    ): List<FeedImage> {
        return imageResults.map { imageResult ->
            FeedImage.create(
                url = imageResult.url,
                width = imageResult.width,
                height = imageResult.height,
                feedId = feed.id!!
            )
        }
    }

    private fun createFeedTags(tags: List<String>, feed: Feed): List<FeedTag> {
        return tags.map { tagTitle ->
            FeedTag.create(feedId = feed.id!!, title = tagTitle)
        }
    }

    private fun recordContestParticipationIfApplicable(
        member: Member,
        feedTags: List<FeedTag>,
        feed: Feed
    ) {
        if (feedTags.isEmpty()) return

        val activeContests = contestRepository.findByStatusAndExpiredAtAfter(
            ContestStatusType.ACTIVE,
            LocalDateTime.now()
        )
        if (activeContests.isEmpty()) return

        val feedTagTitles = feedTags.map { it.title }.toSet()
        val contestTagsMap = contestTagRepository.findByContestIds(activeContests.map { it.id })
            .groupBy { it.contestId }

        activeContests
            .filter { satisfiesContestTags(contestTagsMap[it.id], feedTagTitles) }
            .forEach { recordParticipation(member.id.value, it.id, feed.id!!.value) }
    }

    private fun satisfiesContestTags(contestTags: List<ContestTag>?, feedTagTitles: Set<String>): Boolean {
        val requiredTags = contestTags?.map { it.title }?.toSet() ?: emptySet()
        return requiredTags.isNotEmpty() && feedTagTitles.containsAll(requiredTags)
    }

    private fun recordParticipation(memberPk: Long, contestId: ContestId, feedId: Long) {
        if (contestParticipationRepository.existsByMemberIdAndContestIdAndFeedId(memberPk, contestId, feedId)) {
            return
        }

        contestParticipationRepository.save(
            ContestParticipation.create(contestId = contestId, feedId = feedId, memberId = memberPk)
        )
        activityEventPublisher.publish(ActivityEvent(memberPk, ActivityType.CONTEST_PARTICIPATE))
    }

    private fun buildCreateFeedResult(
        feed: Feed,
        images: List<FeedImage>,
        tags: List<FeedTag>,
        member: Member,
        boardTitle: String
    ): CreateFeedResult {
        return CreateFeedResult(
            id = feed.id!!.value,
            title = feed.title,
            content = feed.content,
            boardId = feed.boardId,
            boardTitle = boardTitle,
            authorId = member.memberId,
            authorNickname = member.nickname,
            authorProfileUrl = member.profileImage,
            images = images.map { FeedImageResult(it.id!!.value, it.url, it.width, it.height) },
            tags = tags.map { it.title },
            likeCount = feed.likeCount,
            commentCount = feed.commentCount,
            createdAt = feed.createdAt
        )
    }
}
