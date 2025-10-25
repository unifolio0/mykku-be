package com.example.mykku.admin.controller

import com.example.mykku.admin.dto.dailymessage.DailyMessageCreateRequest
import com.example.mykku.admin.dto.dailymessage.DailyMessageListResponse
import com.example.mykku.admin.service.AdminDailyMessageService
import com.example.mykku.common.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/dailymessage")
class AdminDailyMessageController(
    private val adminDailyMessageService: AdminDailyMessageService
) {

    @GetMapping
    fun listPage(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        model: Model
    ): String {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "date")
        )
        val messages = adminDailyMessageService.findAll(pageable)

        model.addAttribute("title", "데일리 메시지 관리")
        model.addAttribute("messages", messages)
        return "admin/dailymessage/list"
    }

    @GetMapping("/create")
    fun createPage(model: Model): String {
        model.addAttribute("title", "데일리 메시지 생성")
        return "admin/dailymessage/create"
    }

    @PostMapping("/api")
    @ResponseBody
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

    @DeleteMapping("/api/{id}")
    @ResponseBody
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
