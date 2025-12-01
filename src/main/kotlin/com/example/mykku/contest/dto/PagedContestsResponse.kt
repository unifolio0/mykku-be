package com.example.mykku.contest.dto

import org.springframework.data.domain.Page

data class PagedContestsResponse(
    val content: List<ContestListResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
) {
    companion object {
        fun from(page: Page<ContestListResponse>): PagedContestsResponse {
            return PagedContestsResponse(
                content = page.content,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isLast = page.isLast
            )
        }
    }
}
