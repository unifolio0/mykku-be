package com.example.mykku.feed.application.usecase

import com.example.mykku.contest.repository.ContestTagRepository
import com.example.mykku.feed.application.dto.AuthorResult
import com.example.mykku.feed.application.dto.FeedDetailResult
import com.example.mykku.feed.application.dto.FeedImageResult
import com.example.mykku.feed.application.dto.GetFeedDetailQuery
import com.example.mykku.feed.application.dto.TagResult
import com.example.mykku.feed.application.port.input.GetFeedDetailUseCase
import com.example.mykku.feed.application.port.output.FeedImageRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.port.output.FeedTagRepository
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.like.tool.LikeFeedReader
import com.example.mykku.scrap.tool.SaveFeedReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFeedDetailUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val contestTagRepository: ContestTagRepository,
    private val likeFeedReader: LikeFeedReader,
    private val saveFeedReader: SaveFeedReader
) : GetFeedDetailUseCase {

    override fun execute(query: GetFeedDetailQuery): FeedDetailResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(query.feedId))

        val feedImages = feedImageRepository.findByFeed(feed)
        val feedTags = feedTagRepository.findByFeed(feed)

        val contestTagTitles = getContestTagTitles(feedTags.map { it.title })

        val legacyFeed = createLegacyFeed(feed)
        val isLiked = query.memberId?.let { likeFeedReader.isLiked(it, legacyFeed) } ?: false
        val isSaved = query.memberId?.let { saveFeedReader.isSaved(it, legacyFeed) } ?: false

        return FeedDetailResult(
            id = feed.id!!,
            author = AuthorResult(
                memberId = feed.member.memberId,
                nickname = feed.member.nickname,
                profileImage = feed.member.profileImage,
                role = feed.member.role?.name ?: ""
            ),
            boardId = feed.board.id!!,
            boardTitle = feed.board.title,
            createdAt = feed.createdAt,
            updatedAt = feed.updatedAt,
            title = feed.title,
            content = feed.content,
            images = feedImages.map { FeedImageResult(it.id!!, it.url, it.width, it.height) },
            tags = feedTags.map { TagResult(it.title, contestTagTitles.contains(it.title)) },
            likeCount = feed.likeCount,
            isLiked = isLiked,
            isSaved = isSaved,
            commentCount = feed.commentCount
        )
    }

    private fun getContestTagTitles(tagTitles: List<String>): Set<String> {
        val contestTags = contestTagRepository.findAllByTitleIn(tagTitles)
        return contestTags.map { it.title }.toSet()
    }

    private fun createLegacyFeed(feed: com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity): com.example.mykku.feed.domain.Feed {
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
