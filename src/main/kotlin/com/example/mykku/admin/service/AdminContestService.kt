package com.example.mykku.admin.service

import com.example.mykku.admin.dto.contest.ContestCreateRequest
import com.example.mykku.contest.adapter.input.web.CreateContestResponse
import com.example.mykku.contest.application.dto.ContestImageCommand
import com.example.mykku.contest.application.dto.CreateContestCommand
import com.example.mykku.contest.application.port.input.CreateContestUseCase
import com.example.mykku.image.ImageUploadService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminContestService(
    private val createContestUseCase: CreateContestUseCase,
    private val imageUploadService: ImageUploadService
) {

    @Transactional
    fun create(request: ContestCreateRequest): CreateContestResponse {
        val uploadResult = imageUploadService.uploadEntityImages(
            request.thumbnailImage,
            request.images,
            "contest-images"
        )

        val command = CreateContestCommand(
            title = request.title,
            description = request.description,
            startedAt = request.startedAt,
            expiredAt = request.expiredAt,
            thumbnailUrl = uploadResult.thumbnailUrl,
            images = uploadResult.imageUrls.mapIndexed { index, url ->
                ContestImageCommand(url = url, orderIndex = index)
            },
            tags = request.tags?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        )

        val result = createContestUseCase.execute(command)
        return CreateContestResponse.from(result)
    }
}
