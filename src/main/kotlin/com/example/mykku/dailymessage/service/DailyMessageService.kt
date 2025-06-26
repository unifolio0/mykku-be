package com.example.mykku.dailymessage.service

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.dto.DailyMessageResponse
import com.example.mykku.dailymessage.tool.DailyMessageReader
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DailyMessageService(
    private val dailyMessageReader: DailyMessageReader
) {
    fun getDailyMessages(date: LocalDate, limit: Int, sort: SortDirection): List<DailyMessageResponse> {
        val dailyMessages = dailyMessageReader.getDailyMessages(date, limit, sort)

        return dailyMessages.map { it.toResponse() }
    }

    private fun DailyMessage.toResponse(): DailyMessageResponse {
        return DailyMessageResponse(
            id = this.id!!,
            title = this.title,
            content = this.content,
            date = this.date
        )
    }
}
