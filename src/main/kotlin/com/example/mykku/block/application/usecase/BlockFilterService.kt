package com.example.mykku.block.application.usecase

import com.example.mykku.block.application.port.input.BlockFilterUseCase
import com.example.mykku.block.application.port.output.KeywordBlockRepository
import com.example.mykku.block.application.port.output.MemberBlockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BlockFilterService(
    private val memberBlockRepository: MemberBlockRepository,
    private val keywordBlockRepository: KeywordBlockRepository
) : BlockFilterUseCase {

    override fun getBlockedMemberIds(memberId: String): Set<String> {
        return memberBlockRepository.findBlockedIdsByBlockerId(memberId).toSet()
    }

    override fun getBlockerMemberIds(memberId: String): Set<String> {
        return memberBlockRepository.findBlockerIdsByBlockedId(memberId).toSet()
    }

    override fun getAllBlockRelatedMemberIds(memberId: String): Set<String> {
        val blockedByMe = getBlockedMemberIds(memberId)
        val blockedMe = getBlockerMemberIds(memberId)
        return blockedByMe + blockedMe
    }

    override fun getBlockedKeywords(memberId: String): List<String> {
        return keywordBlockRepository.findKeywordsByMemberId(memberId)
    }

    override fun <T> filterContent(
        items: List<T>,
        memberId: String?,
        memberIdExtractor: (T) -> String,
        contentExtractors: List<(T) -> String?>
    ): List<T> {
        if (memberId == null || items.isEmpty()) return items

        val blockedMemberIds = getAllBlockRelatedMemberIds(memberId)
        val blockedKeywords = getBlockedKeywords(memberId)

        return items.filter { item ->
            val authorId = memberIdExtractor(item)
            if (authorId in blockedMemberIds) return@filter false

            val hasBlockedKeyword = contentExtractors.any { extractor ->
                val content = extractor(item)?.lowercase() ?: return@any false
                blockedKeywords.any { keyword -> content.contains(keyword) }
            }

            !hasBlockedKeyword
        }
    }

    override fun <T> filterByBlockedMembers(
        items: List<T>,
        memberId: String?,
        memberIdExtractor: (T) -> String
    ): List<T> {
        if (memberId == null || items.isEmpty()) return items

        val blockedMemberIds = getAllBlockRelatedMemberIds(memberId)

        return items.filter { item ->
            val authorId = memberIdExtractor(item)
            authorId !in blockedMemberIds
        }
    }

    override fun <T> filterByBlockedKeywords(
        items: List<T>,
        memberId: String?,
        contentExtractors: List<(T) -> String?>
    ): List<T> {
        if (memberId == null || items.isEmpty()) return items

        val blockedKeywords = getBlockedKeywords(memberId)
        if (blockedKeywords.isEmpty()) return items

        return items.filter { item ->
            val hasBlockedKeyword = contentExtractors.any { extractor ->
                val content = extractor(item)?.lowercase() ?: return@any false
                blockedKeywords.any { keyword -> content.contains(keyword) }
            }
            !hasBlockedKeyword
        }
    }
}
