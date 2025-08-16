package com.example.mykku.feed.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateFeedRequestDto(
    @field:NotBlank(message = "제목은 필수입니다")
    val title: String,
    
    @field:NotBlank(message = "내용은 필수입니다")
    @field:Size(max = 1000, message = "내용은 1000자 이하여야 합니다")
    val content: String,
    
    val boardId: Long,
    
    @field:Size(max = 7, message = "태그는 7개 이하여야 합니다")
    val tags: List<String> = emptyList()
)