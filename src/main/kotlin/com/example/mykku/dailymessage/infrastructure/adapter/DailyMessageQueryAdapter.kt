package com.example.mykku.dailymessage.infrastructure.adapter

import com.example.mykku.dailymessage.application.port.out.DailyMessageQueryPort
import com.example.mykku.dailymessage.application.port.out.DailyMessageSummary
import com.example.mykku.dailymessage.domain.model.DailyMessageId
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import org.springframework.stereotype.Component

@Component
class DailyMessageQueryAdapter(
    private val dailyMessageRepository: DailyMessageRepository
) : DailyMessageQueryPort {

    override fun findSummaryById(id: DailyMessageId): DailyMessageSummary? {
        return dailyMessageRepository.findById(id.value)
            .map { dm ->
                DailyMessageSummary(
                    id = dm.id!!,
                    title = dm.title,
                    content = dm.content,
                    date = dm.date
                )
            }
            .orElse(null)
    }

    override fun existsById(id: DailyMessageId): Boolean {
        return dailyMessageRepository.existsById(id.value)
    }
}
