package com.example.mykku.feed.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.repository.FeedImageRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.feed.repository.FeedTagRepository
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class FeedWriter(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository
) {

    @Transactional
    fun createFeed(
        title: String,
        content: String,
        board: Board,
        member: Member,
        imageResults: List<ImageUploadResult>,
        tagTitles: List<String>
    ): Triple<Feed, List<FeedImage>, List<FeedTag>> {
        
        if (imageResults.size > Feed.IMAGE_MAX_COUNT) {
            throw FeedException.feedImageLimitExceeded()
        }

        val normalizedDistinctTags = tagTitles.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        if (normalizedDistinctTags.size > Feed.TAG_MAX_COUNT) {
            throw FeedException.feedTagLimitExceeded()
        }

        val feed = Feed(
            title = title,
            content = content,
            board = board,
            member = member
        )

        val savedFeed = feedRepository.save(feed)

        val feedImages = imageResults.map { imageResult ->
            
            if (imageResult.width <= 0 || imageResult.height <= 0) {
                throw FeedException.imageInvalidDimensions()
            }

            FeedImage(
                url = imageResult.url,
                width = imageResult.width,
                height = imageResult.height,
                feed = savedFeed
            )
        }
        val savedFeedImages = feedImageRepository.saveAll(feedImages)

        val feedTags = normalizedDistinctTags.map { tagTitle ->
            FeedTag(
                feed = savedFeed,
                title = tagTitle
            )
        }
        val savedFeedTags = feedTagRepository.saveAll(feedTags)

        return Triple(savedFeed, savedFeedImages, savedFeedTags)
    }

    fun deleteFeed(feed: Feed) {
        feedTagRepository.deleteAllByFeed(feed)
        feedImageRepository.deleteAllByFeed(feed)
        feedRepository.delete(feed)
    }

    @Transactional
    fun updateFeed(
        feed: Feed,
        title: String?,
        content: String?,
        board: Board?,
        deleteImageIds: List<Long>,
        newImageResults: List<ImageUploadResult>,
        tagTitles: List<String>?
    ): Triple<Feed, List<FeedImage>, List<FeedTag>> {

        title?.let { feed.title = it }
        content?.let {
            if (it.length > Feed.CONTENT_MAX_LENGTH) {
                throw FeedException.feedContentTooLong()
            }
            feed.content = it
        }
        board?.let { feed.board = it }

        if (deleteImageIds.isNotEmpty()) {
            val imagesToDelete = feedImageRepository.findAllByIdInAndFeed(deleteImageIds, feed)
            if (imagesToDelete.size != deleteImageIds.size) {
                throw FeedException.feedImageNotFound()
            }
            feedImageRepository.deleteAll(imagesToDelete)
        }

        if (newImageResults.isNotEmpty()) {
            val newFeedImages = newImageResults.map { imageResult ->
                if (imageResult.width <= 0 || imageResult.height <= 0) {
                    throw FeedException.imageInvalidDimensions()
                }
                FeedImage(
                    url = imageResult.url,
                    width = imageResult.width,
                    height = imageResult.height,
                    feed = feed
                )
            }
            feedImageRepository.saveAll(newFeedImages)
        }

        val updatedTags = if (tagTitles != null) {
            feedTagRepository.deleteAllByFeed(feed)

            val normalizedDistinctTags = tagTitles.asSequence()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .toList()

            if (normalizedDistinctTags.size > Feed.TAG_MAX_COUNT) {
                throw FeedException.feedTagLimitExceeded()
            }

            val feedTags = normalizedDistinctTags.map { tagTitle ->
                FeedTag(feed = feed, title = tagTitle)
            }
            feedTagRepository.saveAll(feedTags)
        } else {
            feedTagRepository.findByFeed(feed)
        }

        val savedFeed = feedRepository.save(feed)
        val remainingImages = feedImageRepository.findByFeed(savedFeed)

        return Triple(savedFeed, remainingImages, updatedTags)
    }
}
