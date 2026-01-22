package com.example.mykku.block.application.usecase

import com.example.mykku.block.application.dto.UnblockKeywordCommand
import com.example.mykku.block.application.port.input.UnblockKeywordUseCase
import com.example.mykku.block.application.port.output.KeywordBlockRepository
import com.example.mykku.block.exception.BlockException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockKeywordService(
    private val keywordBlockRepository: KeywordBlockRepository
) : UnblockKeywordUseCase {

    override fun unblockKeyword(command: UnblockKeywordCommand) {
        val normalizedKeyword = command.keyword.trim().lowercase()

        if (!keywordBlockRepository.existsByMemberIdAndKeyword(command.memberId, normalizedKeyword)) {
            throw BlockException.keywordBlockNotFound()
        }

        keywordBlockRepository.deleteByMemberIdAndKeyword(command.memberId, normalizedKeyword)
    }
}
