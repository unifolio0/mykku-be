package com.example.mykku.feed.dto

import org.springframework.web.multipart.MultipartFile

data class UpdateFeedRequest(
    val title: String?,
    val content: String?,
    val boardId: Long?,
    val tags: List<String>?,
    val deleteImageIds: List<Long>,
    val newImages: List<MultipartFile>
) {
    companion object {
        const val MAX_CONTENT_LENGTH = 1000
        const val MAX_IMAGE_COUNT = 10
        const val MAX_TAG_COUNT = 7

        fun from(dto: UpdateFeedRequestDto, images: List<MultipartFile>?): UpdateFeedRequest {
            return UpdateFeedRequest(
                title = dto.title,
                content = dto.content,
                boardId = dto.boardId,
                tags = dto.tags,
                deleteImageIds = dto.deleteImageIds ?: emptyList(),
                newImages = images ?: emptyList()
            )
        }
    }
}
