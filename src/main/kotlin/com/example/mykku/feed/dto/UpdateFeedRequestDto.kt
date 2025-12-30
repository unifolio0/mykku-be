package com.example.mykku.feed.dto

import jakarta.validation.constraints.Size

data class UpdateFeedRequestDto(
    val title: String?,

    @field:Size(max = MAX_CONTENT_LENGTH, message = "내용은 ${MAX_CONTENT_LENGTH}자 이하여야 합니다")
    val content: String?,

    val boardId: Long?,

    @field:Size(max = MAX_TAG_COUNT, message = "태그는 ${MAX_TAG_COUNT}개 이하여야 합니다")
    val tags: List<String>?,

    val deleteImageIds: List<Long>?
) {
    companion object {
        const val MAX_CONTENT_LENGTH = 1000
        const val MAX_TAG_COUNT = 7
    }
}
