package com.example.mykku.dailymessage.controller

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.dto.DailyMessageResponse
import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.dailymessage.service.DailyMessageService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
    ): ResponseEntity<ApiResponse<List<DailyMessageSummaryResponse>>> {
        val dailyMessages = dailyMessageService.getDailyMessages(date, limit, sort)

        return ResponseEntity.ok(
            ApiResponse(
                message = "하루 덕담 리스트 불러오기에 성공했습니다.",
                data = dailyMessages
            )
        )
    }

    @GetMapping("/api/v1/daily-message/{id}")
    fun getDailyMessage(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<DailyMessageResponse>> {
        val dailyMessage = dailyMessageService.getDailyMessage(id)

        return ResponseEntity.ok(
            ApiResponse(
                message = "하루 덕담 데이터 불러오기에 성공했습니다.",
                data = dailyMessage
            )
        )
    }
}
