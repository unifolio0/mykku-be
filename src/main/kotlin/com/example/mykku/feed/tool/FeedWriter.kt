package com.example.mykku.feed.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
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
        // 이미지 개수 제한 검증
        if (imageResults.size > Feed.IMAGE_MAX_COUNT) {
            throw MykkuException(ErrorCode.FEED_IMAGE_LIMIT_EXCEEDED)
        }

        val normalizedDistinctTags = tagTitles.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        // 태그 개수 제한 검증
        if (normalizedDistinctTags.size > Feed.TAG_MAX_COUNT) {
            throw MykkuException(ErrorCode.FEED_TAG_LIMIT_EXCEEDED)
        }

        val feed = Feed(
            title = title,
            content = content,
            board = board,
            member = member
        )

        val savedFeed = feedRepository.save(feed)

        // Save images using saveAll for better performance
        val feedImages = imageResults.map { imageResult ->
            // 이미지 크기 검증
            if (imageResult.width <= 0 || imageResult.height <= 0) {
                throw MykkuException(ErrorCode.IMAGE_INVALID_DIMENSIONS)
            }

            FeedImage(
                url = imageResult.url,
                width = imageResult.width,
                height = imageResult.height,
                feed = savedFeed
            )
        }
        val savedFeedImages = feedImageRepository.saveAll(feedImages)

        // Save tags using saveAll
        val feedTags = normalizedDistinctTags.map { tagTitle ->
            FeedTag(
                feed = savedFeed,
                title = tagTitle
            )
        }
        val savedFeedTags = feedTagRepository.saveAll(feedTags)

        return Triple(savedFeed, savedFeedImages, savedFeedTags)
    }
}
