package com.example.mykku.admin.controller

import com.example.mykku.admin.dto.dailymessage.DailyMessageCreateRequest
import com.example.mykku.admin.dto.dailymessage.DailyMessageListResponse
import com.example.mykku.admin.service.AdminDailyMessageService
import com.example.mykku.common.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/dailymessage/api")
class AdminDailyMessageApiController(
    private val adminDailyMessageService: AdminDailyMessageService
) {

    @PostMapping
    fun create(
        @Valid @RequestBody request: DailyMessageCreateRequest
    ): ResponseEntity<ApiResponse<DailyMessageListResponse>> {
        val created = adminDailyMessageService.create(request)
        return ResponseEntity.ok(
            ApiResponse(
                message = "데일리 메시지가 생성되었습니다",
                data = created
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Nothing?>> {
        adminDailyMessageService.deleteById(id)
        return ResponseEntity.ok(
            ApiResponse(
                message = "데일리 메시지가 삭제되었습니다",
                data = null
            )
        )
    }
}
