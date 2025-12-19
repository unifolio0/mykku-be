package com.example.mykku.admin.service

import com.example.mykku.admin.dto.dailymessage.DailyMessageCreateRequest
import com.example.mykku.admin.dto.dailymessage.DailyMessageListResponse
import com.example.mykku.dailymessage.application.port.out.DailyMessageQueryPort
import com.example.mykku.dailymessage.application.port.out.DailyMessageRepositoryPort
import com.example.mykku.dailymessage.domain.DailyMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminDailyMessageService(
    private val dailyMessageQueryPort: DailyMessageQueryPort,
    private val dailyMessageRepositoryPort: DailyMessageRepositoryPort
) {

    @Transactional
    fun create(request: DailyMessageCreateRequest): DailyMessageListResponse {
        val dailyMessage = DailyMessage(
            title = request.title,
            content = request.content,
            date = request.date
        )

        val saved = dailyMessageRepositoryPort.save(dailyMessage)
        return DailyMessageListResponse.from(saved)
    }

    fun findAll(pageable: Pageable): Page<DailyMessageListResponse> {
        return dailyMessageQueryPort.findAll(pageable)
            .map { DailyMessageListResponse.from(it) }
    }

    @Transactional
    fun deleteById(id: Long) {
        dailyMessageQueryPort.getDailyMessage(id)
        dailyMessageRepositoryPort.deleteById(id)
    }
}
