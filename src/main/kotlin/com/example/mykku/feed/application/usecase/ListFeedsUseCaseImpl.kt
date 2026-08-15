package com.example.mykku.feed.application.usecase

import com.example.mykku.block.application.port.input.BlockFilterUseCase
import com.example.mykku.feed.application.dto.ListFeedsQuery
import com.example.mykku.feed.application.dto.PagedFeedsResult
import com.example.mykku.feed.application.port.input.ListFeedsUseCase
import com.example.mykku.feed.application.port.output.FeedRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ListFeedsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedResultAssembler: FeedResultAssembler,
    private val blockFilterUseCase: BlockFilterUseCase
) : ListFeedsUseCase {

    override fun execute(query: ListFeedsQuery): PagedFeedsResult {
        val feedPage = feedRepository.findByBoardId(query.boardId, query.pageable)

        val filteredFeeds = blockFilterUseCase.filterContent(
            items = feedPage.content,
            memberId = query.memberId,
            memberIdExtractor = { it.memberId },
            contentExtractors = listOf({ it.title }, { it.content })
        )

        return PagedFeedsResult(
            feeds = feedResultAssembler.assemble(filteredFeeds, query.memberId),
            currentPage = feedPage.number,
            totalPages = feedPage.totalPages,
            totalElements = feedPage.totalElements,
            size = feedPage.size,
            hasNext = feedPage.hasNext(),
            hasPrevious = feedPage.hasPrevious()
        )
    }
}
