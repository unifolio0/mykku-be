package com.example.mykku.home.controller

import com.example.mykku.dailymessage.service.DailyMessageService
import com.example.mykku.home.dto.HomeResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController(
    private val dailyMessageService: DailyMessageService,
) {
    @GetMapping("/api/home")
    fun home(): ResponseEntity<HomeResponse> {
        val todayDailyMessage = dailyMessageService.getTodayDailyMessage()
        val response = HomeResponse(
            dailyMessage = todayDailyMessage.content,
        )
        return ResponseEntity.ok(response)
    }
}
