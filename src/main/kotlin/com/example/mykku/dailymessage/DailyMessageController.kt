package com.example.mykku.dailymessage

import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.common.util.PageableValidator
import com.example.mykku.dailymessage.dto.DailyMessageResponse
import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/daily-messages")
class DailyMessageController(
    private val dailyMessageService: DailyMessageService
) {
    @GetMapping
    fun getDailyMessages(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<DailyMessageSummaryResponse>>> {
        val pageable = PageableValidator.validateAndCreate(
            page,
            size,
            "date",
            Sort.Direction.DESC
        )
        val dailyMessages = dailyMessageService.getDailyMessages(date, pageable)

        return ResponseEntity.ok(
            ApiResponse(
                message = "하루 덕담 리스트 불러오기에 성공했습니다.",
                data = dailyMessages
            )
        )
    }

    @GetMapping("/{id}")
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
