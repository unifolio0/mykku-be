package com.example.mykku.dailymessage.tool

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DailyMessageWriter(
    private val dailyMessageRepository: DailyMessageRepository
) {

    @Transactional
    fun save(dailyMessage: DailyMessage): DailyMessage {
        return dailyMessageRepository.save(dailyMessage)
    }

    @Transactional
    fun deleteById(id: Long) {
        dailyMessageRepository.deleteById(id)
    }
}
