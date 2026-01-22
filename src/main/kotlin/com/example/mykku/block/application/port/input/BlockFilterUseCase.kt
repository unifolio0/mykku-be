package com.example.mykku.block.application.port.input

interface BlockFilterUseCase {
    fun getBlockedMemberIds(memberId: String): Set<String>
    fun getBlockerMemberIds(memberId: String): Set<String>
    fun getAllBlockRelatedMemberIds(memberId: String): Set<String>
    fun getBlockedKeywords(memberId: String): List<String>

    fun <T> filterContent(
        items: List<T>,
        memberId: String?,
        memberIdExtractor: (T) -> String,
        contentExtractors: List<(T) -> String?>
    ): List<T>

    fun <T> filterByBlockedMembers(
        items: List<T>,
        memberId: String?,
        memberIdExtractor: (T) -> String
    ): List<T>

    fun <T> filterByBlockedKeywords(
        items: List<T>,
        memberId: String?,
        contentExtractors: List<(T) -> String?>
    ): List<T>
}
