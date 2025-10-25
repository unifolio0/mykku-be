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
    private val imageUploadService: ImageUploadService
) {

    @Transactional
    fun create(request: FanNoteCreateRequest): FanNoteDetailResponse {
        val coverImageUrl = request.coverImage
            ?.takeIf { !it.isEmpty }
            ?.let { imageUploadService.uploadImage(it).url }

        val fanNote = FanNote(
            title = request.title,
            subtitle = request.subtitle,
            content = request.content,
            productionDate = request.productionDate,
            coverImageUrl = coverImageUrl
        )

        val savedFanNote = fanNoteWriter.save(fanNote)

        val pages = request.pageImages
            ?.filterNot { it.isEmpty }
            ?.mapIndexed { index, file ->
                val imageUrl = imageUploadService.uploadImage(file).url
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
