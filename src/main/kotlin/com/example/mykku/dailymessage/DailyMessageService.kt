package com.example.mykku.dailymessage

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.dto.DailyMessageResponse
import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.dailymessage.tool.DailyMessageReader
import java.time.LocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyMessageService(
    private val dailyMessageReader: DailyMessageReader,
) {
    @Transactional(readOnly = true)
    fun getDailyMessages(date: LocalDate, pageable: Pageable): Page<DailyMessageSummaryResponse> {
        val dailyMessagesPage = dailyMessageReader.getDailyMessagesWithPagination(date, pageable)

        return dailyMessagesPage.map { it.toResponse() }
    }

    private fun DailyMessage.toResponse(): DailyMessageSummaryResponse {
        return DailyMessageSummaryResponse(
            id = this.id!!,
            title = this.title,
            content = this.content,
            date = this.date
        )
    }

    @Transactional(readOnly = true)
    fun getDailyMessage(id: Long): DailyMessageResponse {
        val dailyMessage = dailyMessageReader.getDailyMessage(id)

        return DailyMessageResponse(
            id = dailyMessage.id!!,
            title = dailyMessage.title,
            content = dailyMessage.content,
            createdAt = dailyMessage.createdAt
        )
    }
}
