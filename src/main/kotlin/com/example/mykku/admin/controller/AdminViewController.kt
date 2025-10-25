package com.example.mykku.admin.controller

import com.example.mykku.admin.config.AdminInterceptor
import com.example.mykku.admin.exception.AdminException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminViewController(
    private val adminInterceptor: AdminInterceptor
) {

    @GetMapping("/login")
    fun loginPage(model: Model): String {
        model.addAttribute("title", "로그인")
        return "admin/login"
    }

    @PostMapping("/api/login")
    fun login(
        @RequestParam token: String,
        request: HttpServletRequest
    ): String {
        val authenticated = adminInterceptor.authenticate(token, request)
        if (!authenticated) {
            throw AdminException.invalidToken()
        }
        return "redirect:/admin"
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        adminInterceptor.logout(request)
        return "redirect:/admin/login"
    }

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("title", "대시보드")
        return "admin/index"
    }
}
