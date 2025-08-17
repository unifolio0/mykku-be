package com.example.mykku.feed.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile

data class CreateFeedRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    val title: String,
    
    @field:NotBlank(message = "내용은 필수입니다")
    @field:Size(max = MAX_CONTENT_LENGTH, message = "내용은 ${MAX_CONTENT_LENGTH}자 이하여야 합니다")
    val content: String,
    
    val boardId: Long,
    
    @field:Size(max = MAX_IMAGE_COUNT, message = "이미지는 ${MAX_IMAGE_COUNT}개 이하여야 합니다")
    val images: List<MultipartFile> = emptyList(),
    
    @field:Size(max = MAX_TAG_COUNT, message = "태그는 ${MAX_TAG_COUNT}개 이하여야 합니다")
    val tags: List<String> = emptyList()
) {
    companion object {
        const val MAX_CONTENT_LENGTH = 1000
        const val MAX_IMAGE_COUNT = 10
        const val MAX_TAG_COUNT = 7
    }
}