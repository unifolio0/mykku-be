package com.example.mykku.feed.application.usecase

import com.example.mykku.board.adapter.output.persistence.BoardJpaRepository
import com.example.mykku.contest.repository.ContestTagRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedImageJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedTagJpaEntity
import com.example.mykku.feed.application.dto.AuthorResult
import com.example.mykku.feed.application.dto.FeedDetailResult
import com.example.mykku.feed.application.dto.FeedImageResult
import com.example.mykku.feed.application.dto.TagResult
import com.example.mykku.feed.application.dto.UpdateFeedCommand
import com.example.mykku.feed.application.port.input.UpdateFeedUseCase
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.image.ImageUploadService
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.tool.SaveFeedReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateFeedUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val boardJpaRepository: BoardJpaRepository,
    private val contestTagRepository: ContestTagRepository,
    private val imageUploadService: ImageUploadService,
    private val likeFeedReader: LikeFeedReader,
    private val saveFeedReader: SaveFeedReader
) : UpdateFeedUseCase {

    override fun execute(command: UpdateFeedCommand, member: Member): FeedDetailResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(command.feedId))
        validateFeedOwner(feed, member)
        validateFinalImageCount(feed, command)

        val board = command.boardId?.let {
            boardJpaRepository.findById(it).orElseThrow { IllegalArgumentException("Board not found") }
        }

        feed.update(command.title, command.content, board)

        deleteImages(command.deleteImageIds, feed)
        val newImageResults = uploadImages(command.newImages)
        addNewImages(newImageResults, feed)
        val updatedTags = updateTags(command.tags, feed)

        val savedFeed = feedRepository.save(feed)
        val remainingImages = feedImageRepository.findByFeed(savedFeed)

        val contestTagTitles = getContestTagTitles(updatedTags.map { it.title })
        val legacyFeed = createLegacyFeed(savedFeed)
        val isLiked = likeFeedReader.isLiked(member.id, legacyFeed)
        val isSaved = saveFeedReader.isSaved(member.id, legacyFeed)

        return FeedDetailResult(
            id = savedFeed.id!!,
            author = AuthorResult(
                memberId = member.memberId,
                nickname = member.nickname,
                profileImage = member.profileImage,
                role = member.role?.name ?: ""
            ),
            boardId = savedFeed.board.id!!,
            boardTitle = savedFeed.board.title,
            createdAt = savedFeed.createdAt,
            updatedAt = savedFeed.updatedAt,
            title = savedFeed.title,
            content = savedFeed.content,
            images = remainingImages.map { FeedImageResult(it.id!!, it.url, it.width, it.height) },
            tags = updatedTags.map { TagResult(it.title, contestTagTitles.contains(it.title)) },
            likeCount = savedFeed.likeCount,
            isLiked = isLiked,
            isSaved = isSaved,
            commentCount = savedFeed.commentCount
        )
    }

    private fun validateFeedOwner(feed: FeedJpaEntity, member: Member) {
        if (feed.member.id != member.id) {
            throw FeedException.feedForbiddenAccess()
        }
    }

    private fun validateFinalImageCount(feed: FeedJpaEntity, command: UpdateFeedCommand) {
        val existingImages = feedImageRepository.findByFeed(feed)
        val existingImageCount = existingImages.size
        val deleteImageCount = command.deleteImageIds.size
        val newImageCount = command.newImages.size
        val finalImageCount = existingImageCount - deleteImageCount + newImageCount

        if (finalImageCount > FeedJpaEntity.IMAGE_MAX_COUNT) {
            throw FeedException.feedImageLimitExceeded()
        }
    }

    private fun deleteImages(deleteImageIds: List<Long>, feed: FeedJpaEntity) {
        if (deleteImageIds.isEmpty()) return

        val imagesToDelete = feedImageRepository.findAllByIdInAndFeed(deleteImageIds, feed)
        if (imagesToDelete.size != deleteImageIds.size) {
            throw FeedException.feedImageNotFound()
        }
        feedImageRepository.deleteAll(imagesToDelete)
    }

    private fun uploadImages(images: List<org.springframework.web.multipart.MultipartFile>): List<ImageUploadResult> {
        return if (images.isEmpty()) emptyList()
        else imageUploadService.uploadImages(images)
    }

    private fun addNewImages(imageResults: List<ImageUploadResult>, feed: FeedJpaEntity) {
        if (imageResults.isEmpty()) return

        val newFeedImages = imageResults.map { imageResult ->
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
        feedImageRepository.saveAll(newFeedImages)
    }

    private fun updateTags(tags: List<String>?, feed: FeedJpaEntity): List<FeedTagJpaEntity> {
        if (tags == null) {
            return feedTagRepository.findByFeed(feed)
        }

        feedTagRepository.deleteAllByFeed(feed)

        val normalizedDistinctTags = tags.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        if (normalizedDistinctTags.size > FeedJpaEntity.TAG_MAX_COUNT) {
            throw FeedException.feedTagLimitExceeded()
        }

        val feedTags = normalizedDistinctTags.map { tagTitle ->
            FeedTagJpaEntity(feed = feed, title = tagTitle)
        }
        return feedTagRepository.saveAll(feedTags)
    }

    private fun getContestTagTitles(tagTitles: List<String>): Set<String> {
        val contestTags = contestTagRepository.findAllByTitleIn(tagTitles)
        return contestTags.map { it.title }.toSet()
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
