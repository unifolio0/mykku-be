package com.example.mykku.event.dto

import org.springframework.data.domain.Page

data class PagedEventsResponse(
    val events: List<EventListResponse>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        fun from(page: Page<EventListResponse>): PagedEventsResponse {
            return PagedEventsResponse(
                events = page.content,
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
