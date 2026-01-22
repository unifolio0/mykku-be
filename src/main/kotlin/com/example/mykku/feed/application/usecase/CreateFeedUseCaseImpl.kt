package com.example.mykku.feed.application.usecase

import com.example.mykku.board.adapter.output.persistence.BoardJpaRepository
import com.example.mykku.contest.repository.ContestTagRepository
import com.example.mykku.contest.tool.ContestParticipationReader
import com.example.mykku.contest.tool.ContestParticipationWriter
import com.example.mykku.contest.tool.ContestReader
import com.example.mykku.feed.adapter.output.persistence.entity.FeedImageJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedTagJpaEntity
import com.example.mykku.feed.application.dto.CreateFeedCommand
import com.example.mykku.feed.application.dto.CreateFeedResult
import com.example.mykku.feed.application.dto.FeedImageResult
import com.example.mykku.feed.application.port.input.CreateFeedUseCase
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateFeedUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val boardJpaRepository: BoardJpaRepository,
    private val imageUploadService: ImageUploadService,
    private val contestReader: ContestReader,
    private val contestParticipationWriter: ContestParticipationWriter,
    private val contestParticipationReader: ContestParticipationReader
) : CreateFeedUseCase {

    override fun execute(command: CreateFeedCommand, member: Member): CreateFeedResult {
        val board = boardJpaRepository.findById(command.boardId)
            .orElseThrow { IllegalArgumentException("Board not found") }

        val imageResults = uploadImages(command.images)
        validateImageCount(imageResults.size)

        val normalizedTags = normalizeTags(command.tags)
        validateTagCount(normalizedTags.size)

        val feedJpaEntity = FeedJpaEntity(
            title = command.title,
            content = command.content,
            board = board,
            member = member
        )
        val savedFeed = feedRepository.save(feedJpaEntity)

        val feedImages = createFeedImages(imageResults, savedFeed)
        val savedImages = feedImageRepository.saveAll(feedImages)

        val feedTags = createFeedTags(normalizedTags, savedFeed)
        val savedTags = feedTagRepository.saveAll(feedTags)

        recordContestParticipationIfApplicable(member, savedTags, savedFeed)

        return buildCreateFeedResult(savedFeed, savedImages, savedTags, member)
    }

    private fun uploadImages(images: List<org.springframework.web.multipart.MultipartFile>): List<ImageUploadResult> {
        return if (images.isEmpty()) emptyList()
        else imageUploadService.uploadImages(images)
    }

    private fun validateImageCount(count: Int) {
        if (count > FeedJpaEntity.IMAGE_MAX_COUNT) {
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
        if (count > FeedJpaEntity.TAG_MAX_COUNT) {
            throw FeedException.feedTagLimitExceeded()
        }
    }

    private fun createFeedImages(
        imageResults: List<ImageUploadResult>,
        feed: FeedJpaEntity
    ): List<FeedImageJpaEntity> {
        return imageResults.map { imageResult ->
            if (imageResult.width <= 0 || imageResult.height <= 0) {
                throw FeedException.imageInvalidDimensions()
            }
            FeedImageJpaEntity(
                url = imageResult.url,
                width = imageResult.width,
                height = imageResult.height,
                feed = feed
            )
        }
    }

    private fun createFeedTags(tags: List<String>, feed: FeedJpaEntity): List<FeedTagJpaEntity> {
        return tags.map { tagTitle ->
            FeedTagJpaEntity(feed = feed, title = tagTitle)
        }
    }

    private fun recordContestParticipationIfApplicable(
        member: Member,
        feedTags: List<FeedTagJpaEntity>,
        feed: FeedJpaEntity
    ) {
        if (feedTags.isEmpty()) return

        val feedTagTitles = feedTags.map { it.title }.toSet()
        val activeContestsWithTags = contestReader.getActiveContestsWithAllTags()

        activeContestsWithTags
            .filter { (_, requiredTags) ->
                requiredTags.isNotEmpty() && feedTagTitles.containsAll(requiredTags)
            }
            .forEach { (contest, _) ->
                val legacyFeed = createLegacyFeedForContest(feed)
                if (!contestParticipationReader.existsByMemberAndContestAndFeed(member, contest, legacyFeed)) {
                    contestParticipationWriter.participateViaFeed(member, contest, legacyFeed)
                }
            }
    }

    private fun createLegacyFeedForContest(feed: FeedJpaEntity): com.example.mykku.feed.domain.Feed {
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

    private fun buildCreateFeedResult(
        feed: FeedJpaEntity,
        images: List<FeedImageJpaEntity>,
        tags: List<FeedTagJpaEntity>,
        member: Member
    ): CreateFeedResult {
        return CreateFeedResult(
            id = feed.id!!,
            title = feed.title,
            content = feed.content,
            boardId = feed.board.id!!,
            boardTitle = feed.board.title,
            authorId = member.memberId,
            authorNickname = member.nickname,
            authorProfileUrl = member.profileImage,
            images = images.map { FeedImageResult(it.id!!, it.url, it.width, it.height) },
            tags = tags.map { it.title },
            likeCount = feed.likeCount,
            commentCount = feed.commentCount,
            createdAt = feed.createdAt
        )
    }
}
