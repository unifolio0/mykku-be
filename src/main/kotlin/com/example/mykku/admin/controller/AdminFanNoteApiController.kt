package com.example.mykku.admin.controller

import com.example.mykku.admin.dto.fannote.FanNoteCreateRequest
import com.example.mykku.admin.service.AdminFanNoteService
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.fannote.dto.FanNoteDetailResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/fannote/api")
class AdminFanNoteApiController(
    private val adminFanNoteService: AdminFanNoteService
) {
    
    @PostMapping(consumes = ["multipart/form-data"])
    fun create(
        @Valid @ModelAttribute request: FanNoteCreateRequest
    ): ResponseEntity<ApiResponse<FanNoteDetailResponse>> {
        val created = adminFanNoteService.create(request)
        return ResponseEntity.ok(
            ApiResponse(
                message = "팬노트가 생성되었습니다",
                data = created
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing?>> {
        adminFanNoteService.deleteById(id)
        return ResponseEntity.ok(
            ApiResponse(
                message = "팬노트가 삭제되었습니다",
                data = null
            )
        )
    }
}
