package com.example.mykku.feed.application.dto

import org.springframework.web.multipart.MultipartFile

data class UpdateFeedCommand(
    val feedId: Long,
    val memberId: Long,
    val title: String?,
    val content: String?,
    val boardId: Long?,
    val tags: List<String>?,
    val deleteImageIds: List<Long>,
    val newImages: List<MultipartFile>
)
