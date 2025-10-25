package com.example.mykku.admin.controller

import com.example.mykku.admin.dto.fannote.FanNoteCreateRequest
import com.example.mykku.admin.service.AdminFanNoteService
import com.example.mykku.common.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/fannote")
class AdminFanNoteController(
    private val adminFanNoteService: AdminFanNoteService
) {

    @GetMapping
    fun list(
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable,
        model: Model
    ): String {
        val fanNotes = adminFanNoteService.findAll(pageable)
        model.addAttribute("fanNotes", fanNotes)
        return "admin/fannote/list"
    }

    @GetMapping("/create")
    fun createForm(model: Model): String {
        return "admin/fannote/create"
    }

    @PostMapping("/api", consumes = ["multipart/form-data"])
    @ResponseBody
    fun create(
        @Valid @ModelAttribute request: FanNoteCreateRequest
    ): ResponseEntity<ApiResponse<Any>> {
        adminFanNoteService.create(request)
        return ResponseEntity.ok(ApiResponse(message = "팬노트가 생성되었습니다"))
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<Any>> {
        adminFanNoteService.deleteById(id)
        return ResponseEntity.ok(ApiResponse(message = "팬노트가 삭제되었습니다"))
    }
}
