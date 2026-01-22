package com.example.mykku.block.application.usecase

import com.example.mykku.block.application.dto.GetKeywordBlocksQuery
import com.example.mykku.block.application.dto.KeywordBlockListResult
import com.example.mykku.block.application.dto.KeywordBlockResult
import com.example.mykku.block.application.port.input.GetKeywordBlocksUseCase
import com.example.mykku.block.application.port.output.KeywordBlockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetKeywordBlocksService(
    private val keywordBlockRepository: KeywordBlockRepository
) : GetKeywordBlocksUseCase {

    override fun getKeywordBlocks(query: GetKeywordBlocksQuery): KeywordBlockListResult {
        val page = keywordBlockRepository.findAllByMemberId(query.memberId, query.pageable)

        val blocks = page.content.map { KeywordBlockResult.from(it) }

        return KeywordBlockListResult(
            blocks = blocks,
            totalCount = page.totalElements,
            hasNext = page.hasNext()
        )
    }
}
