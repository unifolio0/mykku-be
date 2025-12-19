package com.example.mykku.admin.service

import com.example.mykku.admin.dto.fannote.FanNoteCreateRequest
import com.example.mykku.fannote.application.port.out.FanNoteQueryPort
import com.example.mykku.fannote.application.port.out.FanNoteRepositoryPort
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.dto.FanNoteDetailResponse
import com.example.mykku.fannote.dto.FanNoteListResponse
import com.example.mykku.image.ImageUploadService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminFanNoteService(
    private val fanNoteQueryPort: FanNoteQueryPort,
    private val fanNoteRepositoryPort: FanNoteRepositoryPort,
    private val s3ImageUploadService: ImageUploadService
) {

    @Transactional
    fun create(request: FanNoteCreateRequest): FanNoteDetailResponse {

        val uploadResult = s3ImageUploadService.uploadFanNoteImages(
            request.coverImage,
            request.pageImages
        )

        val fanNote = FanNote(
            title = request.title,
            subtitle = request.subtitle,
            content = request.content,
            productionDate = request.productionDate,
            coverImageUrl = uploadResult.coverImageUrl
        )

        val savedFanNote = fanNoteRepositoryPort.save(fanNote)

        val pages = uploadResult.pageImageUrls.mapIndexed { index, imageUrl ->
            FanNotePage(
                pageNumber = index + 1,
                imageUrl = imageUrl,
                fanNote = savedFanNote
            )
        }

        if (pages.isNotEmpty()) {
            fanNoteRepositoryPort.saveAllPages(pages)
        }

        return FanNoteDetailResponse.from(savedFanNote, pages)
    }

    fun findAll(pageable: Pageable): Page<FanNoteListResponse> {
        return fanNoteQueryPort.findAllWithPagination(pageable)
            .map { FanNoteListResponse.from(it) }
    }

    fun findById(id: Long): FanNoteDetailResponse {
        val fanNote = fanNoteQueryPort.findById(id)
        val pages = fanNoteQueryPort.findPagesByFanNoteId(id)
        return FanNoteDetailResponse.from(fanNote, pages)
    }

    @Transactional
    fun deleteById(id: Long) {
        fanNoteQueryPort.findById(id)
        fanNoteRepositoryPort.deleteById(id)
    }
}
