package com.example.mykku.admin.service

import com.example.mykku.admin.dto.fannote.FanNoteCreateRequest
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.domain.FanNotePage
import com.example.mykku.fannote.dto.FanNoteDetailResponse
import com.example.mykku.fannote.dto.FanNoteListResponse
import com.example.mykku.fannote.tool.FanNoteReader
import com.example.mykku.fannote.tool.FanNoteWriter
import com.example.mykku.image.FanNoteImageUploadService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminFanNoteService(
    private val fanNoteReader: FanNoteReader,
    private val fanNoteWriter: FanNoteWriter,
    private val fanNoteImageUploadService: FanNoteImageUploadService
) {

    @Transactional
    fun create(request: FanNoteCreateRequest): FanNoteDetailResponse {
        // 1. 팬노트별 고유 폴더 생성 (yyyy-MM-dd-uuid)
        val folderName = fanNoteImageUploadService.createFanNoteFolder()

        // 2. 커버 이미지 업로드 (pageNumber = 0)
        val coverImageUrl = request.coverImage
            ?.takeIf { !it.isEmpty }
            ?.let { fanNoteImageUploadService.uploadImage(it, folderName, 0).url }

        // 3. FanNote 엔티티 저장
        val fanNote = FanNote(
            title = request.title,
            subtitle = request.subtitle,
            content = request.content,
            productionDate = request.productionDate,
            coverImageUrl = coverImageUrl
        )

        val savedFanNote = fanNoteWriter.save(fanNote)

        // 4. 페이지 이미지 업로드 (pageNumber = 1, 2, 3, ...)
        val pages = request.pageImages
            ?.filterNot { it.isEmpty }
            ?.mapIndexed { index, file ->
                val imageUrl = fanNoteImageUploadService.uploadImage(
                    file,
                    folderName,
                    index + 1  // 1부터 시작
                ).url
                FanNotePage(
                    pageNumber = index + 1,
                    imageUrl = imageUrl,
                    fanNote = savedFanNote
                )
            } ?: emptyList()

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
