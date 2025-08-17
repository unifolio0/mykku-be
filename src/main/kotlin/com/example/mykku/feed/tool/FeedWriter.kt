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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FeedWriter(
    private val feedRepository: FeedRepository,
    private val tagRepository: TagRepository
) {
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
        
        imageResults.forEach { imageResult ->
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

        normalizedDistinctTags.forEach { tagTitle ->
            val tag = findOrCreateTag(tagTitle)
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
            // 동시성 문제로 이미 태그가 생성된 경우 다시 조회
            tagRepository.findByTitle(title) 
                ?: throw MykkuException(ErrorCode.TAG_CREATION_FAILED)
        }
    }
}