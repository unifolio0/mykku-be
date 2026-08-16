package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.GetMyFeedsQuery
import com.example.mykku.feed.application.dto.PagedFeedsResult
import com.example.mykku.feed.application.port.input.GetMyFeedsUseCase
import com.example.mykku.feed.application.port.output.FeedRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetMyFeedsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedResultAssembler: FeedResultAssembler
) : GetMyFeedsUseCase {

    override fun execute(query: GetMyFeedsQuery): PagedFeedsResult {
        val feedPage = feedRepository.findByMemberId(query.memberId, query.pageable)

        return PagedFeedsResult(
            feeds = feedResultAssembler.assemble(feedPage.content, query.memberId),
            currentPage = feedPage.number,
            totalPages = feedPage.totalPages,
            totalElements = feedPage.totalElements,
            size = feedPage.size,
            hasNext = feedPage.hasNext(),
            hasPrevious = feedPage.hasPrevious()
        )
    }
}
