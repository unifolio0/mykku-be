package com.example.mykku.contest.dto

import org.springframework.data.domain.Page

data class PagedContestsResponse(
    val contests: List<ContestListResponse>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        fun from(page: Page<ContestListResponse>): PagedContestsResponse {
            return PagedContestsResponse(
                contests = page.content,
                currentPage = page.number,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                size = page.size,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
            )
        }
    }
}
