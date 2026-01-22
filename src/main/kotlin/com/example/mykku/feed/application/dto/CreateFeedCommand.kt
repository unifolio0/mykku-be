package com.example.mykku.feed.application.dto

import org.springframework.web.multipart.MultipartFile

data class CreateFeedCommand(
    val title: String,
    val content: String,
    val boardId: Long,
    val memberId: String,
    val images: List<MultipartFile>,
    val tags: List<String>
)
