package com.example.mykku.feed.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.domain.Tag
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.feed.repository.TagRepository
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.member.domain.Member
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FeedWriter(
    private val feedRepository: FeedRepository,
    private val tagRepository: TagRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(FeedWriter::class.java)
    }
    @Transactional
    fun createFeed(
        title: String,
        content: String,
        board: Board,
        member: Member,
        imageResults: List<ImageUploadResult>,
        tagTitles: List<String>
    ): Feed {
        val feed = Feed(
            title = title,
            content = content,
            board = board,
            member = member
        )
        
        // 이미지 개수 제한 검증 (엔티티 생성 후 추가이므로 여기서 방어)
        if (imageResults.size > Feed.IMAGE_MAX_COUNT) {
            throw MykkuException(ErrorCode.FEED_IMAGE_LIMIT_EXCEEDED)
        }
        
        imageResults.forEach { imageResult ->
            // 이미지 크기 검증
            if (imageResult.width <= 0 || imageResult.height <= 0) {
                throw MykkuException(ErrorCode.IMAGE_INVALID_DIMENSIONS)
            }
            
            val feedImage = FeedImage(
                url = imageResult.url,
                width = imageResult.width,
                height = imageResult.height,
                feed = feed
            )
            feed.feedImages.add(feedImage)
        }
        
        val normalizedDistinctTags = tagTitles.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()

        // 태그 개수 제한 검증 (엔티티 생성 후 추가이므로 여기서 방어)
        if (normalizedDistinctTags.size > Feed.TAG_MAX_COUNT) {
            throw MykkuException(ErrorCode.FEED_TAG_LIMIT_EXCEEDED)
        }

        // 배치 조회로 N+1 쿼리 최적화
        val existingTagsByTitle = tagRepository.findAllByTitleIn(normalizedDistinctTags)
            .associateBy { it.title }
        
        normalizedDistinctTags.forEach { tagTitle ->
            val tag = existingTagsByTitle[tagTitle] ?: findOrCreateTag(tagTitle)
            val feedTag = FeedTag(
                feed = feed,
                tag = tag
            )
            feed.feedTags.add(feedTag)
        }
        
        return feedRepository.save(feed)
    }
    
    private fun findOrCreateTag(title: String): Tag {
        return tagRepository.findByTitle(title) ?: try {
            tagRepository.save(Tag(title = title))
        } catch (e: DataIntegrityViolationException) {
            logger.debug("Tag creation failed due to constraint violation for title: '$title'", e)
            // 동시성 문제로 이미 태그가 생성된 경우 다시 조회
            tagRepository.findByTitle(title) 
                ?: throw MykkuException(ErrorCode.TAG_CREATION_FAILED)
        }
    }
}