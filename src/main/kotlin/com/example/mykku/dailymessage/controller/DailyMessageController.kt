package com.example.mykku.dailymessage.controller

import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.dto.DailyMessageResponse
import com.example.mykku.dailymessage.service.DailyMessageService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class DailyMessageController(
    private val dailyMessageService: DailyMessageService
) {
    @GetMapping("/api/v1/daily-messages")
    fun getDailyMessages(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "DESC") sort: SortDirection
    ): List<DailyMessageResponse> {
        return dailyMessageService.getDailyMessages(date, limit, sort)
    }
}
