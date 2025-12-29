package com.example.mykku.admin.service

import com.example.mykku.admin.dto.dailymessage.DailyMessageCreateRequest
import com.example.mykku.admin.dto.dailymessage.DailyMessageListResponse
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.dailymessage.tool.DailyMessageWriter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminDailyMessageService(
    private val dailyMessageReader: DailyMessageReader,
    private val dailyMessageWriter: DailyMessageWriter
) {

    @Transactional
    fun create(request: DailyMessageCreateRequest): DailyMessageListResponse {
        val dailyMessage = DailyMessage(
            title = request.title,
            content = request.content,
            date = request.date
        )

        val saved = dailyMessageWriter.save(dailyMessage)
        return DailyMessageListResponse.from(saved)
    }

    fun findAll(pageable: Pageable): Page<DailyMessageListResponse> {
        return dailyMessageReader.findAll(pageable)
            .map { DailyMessageListResponse.from(it) }
    }

    @Transactional
    fun deleteById(id: Long) {
        dailyMessageReader.getDailyMessage(id)
        dailyMessageWriter.deleteById(id)
    }
}
