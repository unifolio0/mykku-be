package com.example.mykku.feed.dto

import org.springframework.data.domain.Page

data class PagedFeedsResponse(
    val feeds: List<FeedResponse>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        fun from(page: Page<FeedResponse>): PagedFeedsResponse {
            return PagedFeedsResponse(
                feeds = page.content,
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