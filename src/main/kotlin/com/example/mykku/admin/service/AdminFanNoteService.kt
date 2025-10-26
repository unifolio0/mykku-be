package com.example.mykku.admin.service

import com.example.mykku.admin.dto.fannote.FanNoteCreateRequest
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.dto.FanNoteDetailResponse
import com.example.mykku.fannote.dto.FanNoteListResponse
import com.example.mykku.fannote.tool.FanNoteReader
import com.example.mykku.fannote.tool.FanNoteWriter
import com.example.mykku.image.ImageUploadService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminFanNoteService(
    private val fanNoteReader: FanNoteReader,
    private val fanNoteWriter: FanNoteWriter,
    private val s3ImageUploadService: ImageUploadService
) {

    @Transactional
    fun create(request: FanNoteCreateRequest): FanNoteDetailResponse {
        // 1. 커버와 페이지 이미지 모두 업로드
        val uploadResult = s3ImageUploadService.uploadFanNoteImages(
            request.coverImage,
            request.pageImages
        )

        // 2. FanNote 엔티티 저장
        val fanNote = FanNote(
            title = request.title,
            subtitle = request.subtitle,
            content = request.content,
            productionDate = request.productionDate,
            coverImageUrl = uploadResult.coverImageUrl
        )

        val savedFanNote = fanNoteWriter.save(fanNote)

        // 3. 페이지 엔티티들 저장
        val pages = uploadResult.pageImageUrls.mapIndexed { index, imageUrl ->
            FanNotePage(
                pageNumber = index + 1,
                imageUrl = imageUrl,
                fanNote = savedFanNote
            )
        }

        if (pages.isNotEmpty()) {
            fanNoteWriter.saveAllPages(pages)
        }

        return FanNoteDetailResponse.from(savedFanNote, pages)
    }

    fun findAll(pageable: Pageable): Page<FanNoteListResponse> {
        return fanNoteReader.findAllWithPagination(pageable)
            .map { FanNoteListResponse.from(it) }
    }

    fun findById(id: Long): FanNoteDetailResponse {
        val fanNote = fanNoteReader.findById(id)
        val pages = fanNoteReader.findPagesByFanNoteId(id)
        return FanNoteDetailResponse.from(fanNote, pages)
    }

    @Transactional
    fun deleteById(id: Long) {
        fanNoteReader.findById(id)
        fanNoteWriter.deleteById(id)
    }
}
