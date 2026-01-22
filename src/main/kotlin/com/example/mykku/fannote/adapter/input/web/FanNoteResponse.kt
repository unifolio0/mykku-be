package com.example.mykku.fannote.adapter.input.web

import com.example.mykku.fannote.application.dto.FanNoteDetailResult
import com.example.mykku.fannote.application.dto.FanNoteListResult
import com.example.mykku.fannote.application.dto.FanNotePageResult

data class FanNoteListResponse(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val content: String?,
    val productionDate: String,
    val coverImageUrl: String?
) {
    companion object {
        fun from(result: FanNoteListResult): FanNoteListResponse {
            return FanNoteListResponse(
                id = result.id,
                title = result.title,
                subtitle = result.subtitle,
                content = result.content,
                productionDate = result.productionDate.toString(),
                coverImageUrl = result.coverImageUrl
            )
        }
    }
}

data class FanNoteDetailResponse(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val content: String?,
    val productionDate: String,
    val coverImageUrl: String?,
    val pages: List<FanNotePageResponse>
) {
    companion object {
        fun from(result: FanNoteDetailResult): FanNoteDetailResponse {
            return FanNoteDetailResponse(
                id = result.id,
                title = result.title,
                subtitle = result.subtitle,
                content = result.content,
                productionDate = result.productionDate.toString(),
                coverImageUrl = result.coverImageUrl,
                pages = result.pages.map { FanNotePageResponse.from(it) }
            )
        }
    }
}

data class FanNotePageResponse(
    val pageNumber: Int,
    val imageUrl: String
) {
    companion object {
        fun from(result: FanNotePageResult): FanNotePageResponse {
            return FanNotePageResponse(
                pageNumber = result.pageNumber,
                imageUrl = result.imageUrl
            )
        }
    }
}
