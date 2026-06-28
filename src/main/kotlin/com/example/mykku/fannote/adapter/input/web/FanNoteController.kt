package com.example.mykku.fannote.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.fannote.application.port.input.GetFanNoteDetailUseCase
import com.example.mykku.fannote.application.port.input.GetFanNoteListUseCase
import com.example.mykku.member.domain.entity.Member
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
    private val getFanNoteListUseCase: GetFanNoteListUseCase,
    private val getFanNoteDetailUseCase: GetFanNoteDetailUseCase
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
        val result = getFanNoteListUseCase.execute(pageable)
        return ApiResponse(
            message = "덕질노트 목록 조회 성공",
            data = result.map { FanNoteListResponse.from(it) }
        )
    }

    @GetMapping("/{fanNoteId}")
    fun getFanNoteDetail(
        @PathVariable fanNoteId: Long,
        @CurrentMember(required = false) member: Member?
    ): ApiResponse<FanNoteDetailResponse> {
        val result = getFanNoteDetailUseCase.execute(fanNoteId, member?.id?.value)
        return ApiResponse(
            message = "덕질노트 상세 조회 성공",
            data = FanNoteDetailResponse.from(result)
        )
    }
}
