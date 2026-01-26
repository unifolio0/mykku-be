package com.example.mykku.feed.application.usecase

import com.example.mykku.board.application.port.output.BoardRepository
import com.example.mykku.board.domain.vo.BoardId
import com.example.mykku.board.exception.BoardException
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestRepository
import com.example.mykku.contest.application.port.output.ContestTagRepository
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
    private val contestParticipationRepository: ContestParticipationRepository
) : CreateFeedUseCase {

    override fun execute(command: CreateFeedCommand, member: Member): CreateFeedResult {
        val board = boardRepository.findById(BoardId(command.boardId))
            ?: throw BoardException.boardNotFound()

        val imageResults = uploadImages(command.images)
        validateImageCount(imageResults.size)

        val normalizedTags = normalizeTags(command.tags)
        validateTagCount(normalizedTags.size)

        val feed = Feed.create(
            title = command.title,
            content = command.content,
            boardId = command.boardId,
            memberId = member.id.value
        )
        val savedFeed = feedRepository.save(feed, command.boardId, member.id.value)

        val feedImages = createFeedImages(imageResults, savedFeed)
        val savedImages = feedImageRepository.saveAll(feedImages, savedFeed.id!!)

        val feedTags = createFeedTags(normalizedTags, savedFeed)
        val savedTags = feedTagRepository.saveAll(feedTags, savedFeed.id!!)

        recordContestParticipationIfApplicable(member, savedTags, savedFeed)

        return buildCreateFeedResult(savedFeed, savedImages, savedTags, member, board.title)
    }

    private fun uploadImages(images: List<org.springframework.web.multipart.MultipartFile>): List<ImageUploadResult> {
        return if (images.isEmpty()) emptyList()
        else imageUploadService.uploadImages(images)
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

        val feedTagTitles = feedTags.map { it.title }.toSet()
        val activeContests = contestRepository.findByStatusAndExpiredAtAfter(
            ContestStatusType.ACTIVE,
            LocalDateTime.now()
        )

        if (activeContests.isEmpty()) return

        val contestIds = activeContests.map { it.id }
        val contestTags = contestTagRepository.findByContestIds(contestIds)
        val contestTagsMap = contestTags.groupBy { it.contestId }

        activeContests.forEach { contest ->
            val requiredTags = contestTagsMap[contest.id]?.map { it.title }?.toSet() ?: emptySet()
            if (requiredTags.isNotEmpty() && feedTagTitles.containsAll(requiredTags)) {
                val memberIdStr = member.id.value
                if (!contestParticipationRepository.existsByMemberIdAndContestIdAndFeedId(memberIdStr, contest.id, feed.id!!.value)) {
                    val participation = com.example.mykku.contest.domain.entity.ContestParticipation.create(
                        contestId = contest.id,
                        feedId = feed.id!!.value,
                        memberId = memberIdStr
                    )
                    contestParticipationRepository.save(participation)
                }
            }
        }
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
