package com.example.mykku.admin.controller

import com.example.mykku.admin.service.AdminBoardService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/board")
class AdminBoardViewController(
    private val adminBoardService: AdminBoardService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("boards", adminBoardService.findAll())
        return "admin/board/list"
    }

    @GetMapping("/create")
    fun createForm(): String {
        return "admin/board/create"
    }
}
