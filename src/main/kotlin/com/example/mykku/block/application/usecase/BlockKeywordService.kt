package com.example.mykku.block.application.usecase

import com.example.mykku.block.application.dto.BlockKeywordCommand
import com.example.mykku.block.application.dto.KeywordBlockResult
import com.example.mykku.block.application.port.input.BlockKeywordUseCase
import com.example.mykku.block.application.port.output.KeywordBlockRepository
import com.example.mykku.block.domain.entity.KeywordBlock
import com.example.mykku.block.exception.BlockException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockKeywordService(
    private val keywordBlockRepository: KeywordBlockRepository
) : BlockKeywordUseCase {

    override fun blockKeyword(command: BlockKeywordCommand): KeywordBlockResult {
        val normalizedKeyword = normalizeKeyword(command.keyword)
        validateKeyword(normalizedKeyword)

        if (keywordBlockRepository.existsByMemberIdAndKeyword(command.memberId, normalizedKeyword)) {
            throw BlockException.keywordAlreadyBlocked()
        }

        if (keywordBlockRepository.countByMemberId(command.memberId) >= KeywordBlock.MAX_KEYWORD_COUNT) {
            throw BlockException.keywordLimitExceeded()
        }

        val keywordBlock = KeywordBlock.create(
            memberId = command.memberId,
            keyword = normalizedKeyword
        )

        val savedBlock = keywordBlockRepository.save(keywordBlock)
        return KeywordBlockResult.from(savedBlock)
    }

    private fun normalizeKeyword(keyword: String): String {
        return keyword.trim().lowercase()
    }

    private fun validateKeyword(keyword: String) {
        if (keyword.isBlank()) {
            throw BlockException.keywordEmpty()
        }
        if (keyword.length > KeywordBlock.KEYWORD_MAX_LENGTH) {
            throw BlockException.keywordTooLong()
        }
    }
}
