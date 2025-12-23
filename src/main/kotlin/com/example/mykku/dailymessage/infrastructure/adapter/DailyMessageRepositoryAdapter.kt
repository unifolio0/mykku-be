package com.example.mykku.dailymessage.infrastructure.adapter

import com.example.mykku.dailymessage.application.port.out.DailyMessageRepositoryPort
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DailyMessageRepositoryAdapter(
    private val dailyMessageRepository: DailyMessageRepository
) : DailyMessageRepositoryPort {

    @Transactional
    override fun save(dailyMessage: DailyMessage): DailyMessage {
        return dailyMessageRepository.save(dailyMessage)
    }

    @Transactional
    override fun deleteById(id: Long) {
        dailyMessageRepository.deleteById(id)
    }
}
