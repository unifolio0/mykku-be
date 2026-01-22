package com.example.mykku.feed.domain.entity

import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.feed.domain.vo.FeedImageId
import com.example.mykku.feed.exception.FeedException
import java.time.LocalDateTime

class FeedImage private constructor(
    val id: FeedImageId?,
    val url: String,
    val width: Int,
    val height: Int,
    val feedId: FeedId,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            url: String,
            width: Int,
            height: Int,
            feedId: FeedId
        ): FeedImage {
            validateDimensions(width, height)
            val now = LocalDateTime.now()
            return FeedImage(
                id = null,
                url = url,
                width = width,
                height = height,
                feedId = feedId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            url: String,
            width: Int,
            height: Int,
            feedId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): FeedImage {
            return FeedImage(
                id = FeedImageId.of(id),
                url = url,
                width = width,
                height = height,
                feedId = FeedId.of(feedId),
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        private fun validateDimensions(width: Int, height: Int) {
            if (width <= 0 || height <= 0) {
                throw FeedException.imageInvalidDimensions()
            }
        }
    }
}
