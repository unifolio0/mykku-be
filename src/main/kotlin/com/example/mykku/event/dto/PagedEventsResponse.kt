package com.example.mykku.event.dto

import org.springframework.data.domain.Page

data class PagedEventsResponse(
    val content: List<EventListResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
) {
    companion object {
        fun from(page: Page<EventListResponse>): PagedEventsResponse {
            return PagedEventsResponse(
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
