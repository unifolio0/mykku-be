package com.example.mykku.feed.application.usecase

import com.example.mykku.contest.application.port.output.ContestTagRepository
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
import com.example.mykku.like.application.port.output.LikeFeedPort
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFeedDetailUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val contestTagRepository: ContestTagRepository,
    private val likeFeedPort: LikeFeedPort,
    private val saveFeedPort: SaveFeedPort
) : GetFeedDetailUseCase {

    override fun execute(query: GetFeedDetailQuery): FeedDetailResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(query.feedId))

        val feedImages = feedImageRepository.findByFeed(feed)
        val feedTags = feedTagRepository.findByFeed(feed)

        val contestTagTitles = getContestTagTitles(feedTags.map { it.title })

        val isLiked = query.memberId?.let { likeFeedPort.existsByMemberIdAndFeedId(it, feed.id!!) } ?: false
        val isSaved = query.memberId?.let { saveFeedPort.existsByMemberIdAndFeedId(it, feed.id!!) } ?: false

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
}
