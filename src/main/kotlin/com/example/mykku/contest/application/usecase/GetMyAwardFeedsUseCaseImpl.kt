package com.example.mykku.contest.application.usecase

import com.example.mykku.contest.application.dto.GetMyAwardFeedsQuery
import com.example.mykku.contest.application.port.input.GetMyAwardFeedsUseCase
import com.example.mykku.contest.application.port.output.ContestParticipationRepository
import com.example.mykku.contest.application.port.output.ContestWinnerRepository
import com.example.mykku.feed.application.dto.PagedFeedsResult
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.application.usecase.FeedResultAssembler
import com.example.mykku.feed.domain.entity.Feed
import com.example.mykku.feed.domain.vo.FeedId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyAwardFeedsUseCaseImpl(
    private val contestWinnerRepository: ContestWinnerRepository,
    private val contestParticipationRepository: ContestParticipationRepository,
    private val feedRepository: FeedRepository,
    private val feedResultAssembler: FeedResultAssembler
) : GetMyAwardFeedsUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetMyAwardFeedsQuery): PagedFeedsResult {
        val feedIds = getAwardFeedIds(query.memberId)
        if (feedIds.isEmpty()) return emptyPagedResult(query)

        val sortedFeeds = feedRepository.findAllByIds(feedIds).sortedByDescending { it.createdAt }
        val pagedFeeds = pageOf(sortedFeeds, query)

        return PagedFeedsResult(
            feeds = feedResultAssembler.assemble(pagedFeeds, query.memberId),
            currentPage = query.pageable.pageNumber,
            totalPages = totalPagesOf(sortedFeeds.size, query.pageable.pageSize),
            totalElements = sortedFeeds.size.toLong(),
            size = query.pageable.pageSize,
            hasNext = endIndexOf(sortedFeeds.size, query) < sortedFeeds.size,
            hasPrevious = query.pageable.pageNumber > 0
        )
    }

    private fun getAwardFeedIds(memberId: Long): List<FeedId> {
        val winners = contestWinnerRepository.findByMemberId(memberId)
        if (winners.isEmpty()) return emptyList()

        val participationIds = winners.map { it.participationId }
        val participations = contestParticipationRepository.findAllByIdIn(participationIds)
        return participations.map { FeedId.of(it.feedId) }.distinct()
    }

    private fun pageOf(feeds: List<Feed>, query: GetMyAwardFeedsQuery): List<Feed> {
        val start = query.pageable.pageNumber * query.pageable.pageSize
        if (start >= feeds.size) return emptyList()
        return feeds.subList(start, endIndexOf(feeds.size, query))
    }

    private fun endIndexOf(totalSize: Int, query: GetMyAwardFeedsQuery): Int {
        val start = query.pageable.pageNumber * query.pageable.pageSize
        return minOf(start + query.pageable.pageSize, totalSize)
    }

    private fun totalPagesOf(totalSize: Int, pageSize: Int): Int {
        if (totalSize == 0) return 0
        return (totalSize + pageSize - 1) / pageSize
    }

    private fun emptyPagedResult(query: GetMyAwardFeedsQuery): PagedFeedsResult {
        return PagedFeedsResult(
            feeds = emptyList(),
            currentPage = query.pageable.pageNumber,
            totalPages = 0,
            totalElements = 0,
            size = query.pageable.pageSize,
            hasNext = false,
            hasPrevious = false
        )
    }
}
