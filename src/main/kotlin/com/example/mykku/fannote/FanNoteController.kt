package com.example.mykku.fannote

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.fannote.dto.FanNoteDetailResponse
import com.example.mykku.fannote.dto.FanNoteListResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/fan-notes")
class FanNoteController(
    private val fanNoteService: FanNoteService
) {

    @GetMapping
    fun getFanNoteList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponse<Page<FanNoteListResponse>> {
        val pageable = PageableValidator.validateAndCreate(
            page = page,
            size = size,
            sortProperty = "productionDate",
            direction = Sort.Direction.DESC
        )
        val fanNotes = fanNoteService.getFanNoteList(pageable)
        return ApiResponse(
            message = "덕질노트 목록 조회 성공",
            data = fanNotes
        )
    }

    @GetMapping("/{fanNoteId}")
    fun getFanNoteDetail(
        @PathVariable fanNoteId: Long
    ): ApiResponse<FanNoteDetailResponse> {
        val fanNoteDetail = fanNoteService.getFanNoteDetail(fanNoteId)
        return ApiResponse(
            message = "덕질노트 상세 조회 성공",
            data = fanNoteDetail
        )
    }
}
