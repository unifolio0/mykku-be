package com.example.mykku.dailymessage.service

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DailyMessageService(
    private val dailyMessageRepository: DailyMessageRepository,
) {
    fun getTodayDailyMessage(): DailyMessage {
        return dailyMessageRepository.getByDate(LocalDate.now())
    }
}
