package com.example.mykku.block.tool

import org.springframework.stereotype.Component

@Component
class BlockFilterHelper(
    private val blockReader: BlockReader
) {

    fun <T> filterContent(
        items: List<T>,
        memberId: String?,
        memberIdExtractor: (T) -> String,
        contentExtractors: List<(T) -> String?>
    ): List<T> {
        if (memberId == null || items.isEmpty()) return items

        val blockedMemberIds = blockReader.getAllBlockRelatedMemberIds(memberId)
        val blockedKeywords = blockReader.getBlockedKeywords(memberId)

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

    fun <T> filterByBlockedMembers(
        items: List<T>,
        memberId: String?,
        memberIdExtractor: (T) -> String
    ): List<T> {
        if (memberId == null || items.isEmpty()) return items

        val blockedMemberIds = blockReader.getAllBlockRelatedMemberIds(memberId)

        return items.filter { item ->
            val authorId = memberIdExtractor(item)
            authorId !in blockedMemberIds
        }
    }

    fun <T> filterByBlockedKeywords(
        items: List<T>,
        memberId: String?,
        contentExtractors: List<(T) -> String?>
    ): List<T> {
        if (memberId == null || items.isEmpty()) return items

        val blockedKeywords = blockReader.getBlockedKeywords(memberId)
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
