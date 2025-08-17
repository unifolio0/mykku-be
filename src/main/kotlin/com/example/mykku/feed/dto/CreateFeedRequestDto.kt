package com.example.mykku.feed.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateFeedRequestDto(
    @field:NotBlank(message = "제목은 필수입니다")
    val title: String,
    
    @field:NotBlank(message = "내용은 필수입니다")
    @field:Size(max = MAX_CONTENT_LENGTH, message = "내용은 ${MAX_CONTENT_LENGTH}자 이하여야 합니다")
    val content: String,
    
    val boardId: Long,
    
    @field:Size(max = MAX_TAG_COUNT, message = "태그는 ${MAX_TAG_COUNT}개 이하여야 합니다")
    val tags: List<String> = emptyList()
) {
    companion object {
        const val MAX_CONTENT_LENGTH = 1000
        const val MAX_TAG_COUNT = 7
    }
}