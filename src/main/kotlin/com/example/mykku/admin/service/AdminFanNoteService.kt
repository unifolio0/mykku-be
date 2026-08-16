package com.example.mykku.admin.service

import com.example.mykku.admin.dto.fannote.FanNoteCreateRequest
import com.example.mykku.fannote.adapter.input.web.FanNoteDetailResponse
import com.example.mykku.fannote.adapter.input.web.FanNoteListResponse
import com.example.mykku.fannote.application.dto.CreateFanNoteCommand
import com.example.mykku.fannote.application.port.input.CreateFanNoteUseCase
import com.example.mykku.fannote.application.port.input.DeleteFanNoteUseCase
import com.example.mykku.fannote.application.port.input.GetFanNoteDetailUseCase
import com.example.mykku.fannote.application.port.input.GetFanNoteListUseCase
import com.example.mykku.image.ImageUploadService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminFanNoteService(
    private val createFanNoteUseCase: CreateFanNoteUseCase,
    private val getFanNoteListUseCase: GetFanNoteListUseCase,
    private val getFanNoteDetailUseCase: GetFanNoteDetailUseCase,
    private val deleteFanNoteUseCase: DeleteFanNoteUseCase,
    private val s3ImageUploadService: ImageUploadService
) {

    @Transactional
    fun create(request: FanNoteCreateRequest): FanNoteDetailResponse {
        val uploadResult = s3ImageUploadService.uploadFanNoteImages(
            request.coverImage,
            request.pageImages
        )

        val command = CreateFanNoteCommand(
            title = request.title,
            subtitle = request.subtitle,
            content = request.content,
            productionDate = request.productionDate,
            coverImageUrl = uploadResult.coverImageUrl,
            pageImageUrls = uploadResult.pageImageUrls
        )

        val result = createFanNoteUseCase.execute(command)
        return FanNoteDetailResponse.from(result)
    }

    fun findAll(pageable: Pageable): Page<FanNoteListResponse> {
        return getFanNoteListUseCase.execute(pageable)
            .map { FanNoteListResponse.from(it) }
    }

    fun findById(id: Long): FanNoteDetailResponse {
        val result = getFanNoteDetailUseCase.execute(id, null)
        return FanNoteDetailResponse.from(result)
    }

    @Transactional
    fun deleteById(id: Long) {
        val imageUrls = collectImageUrls(id)
        deleteFanNoteUseCase.execute(id)
        imageUrls.forEach { deleteImageQuietly(it) }
    }

    private fun collectImageUrls(id: Long): List<String> {
        val detail = getFanNoteDetailUseCase.execute(id, null)
        return listOfNotNull(detail.coverImageUrl) + detail.pages.map { it.imageUrl }
    }

    private fun deleteImageQuietly(url: String) {
        runCatching { s3ImageUploadService.delete(url) }
            .onFailure { log.warn("덕질노트 이미지 삭제에 실패했습니다: {}", url, it) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(AdminFanNoteService::class.java)
    }
}
