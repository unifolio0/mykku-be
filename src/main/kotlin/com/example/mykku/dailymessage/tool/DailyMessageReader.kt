package com.example.mykku.dailymessage.tool

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DailyMessageReader(
    private val dailyMessageRepository: DailyMessageRepository,
) {
    fun getTodayDailyMessage(): DailyMessage {
        return dailyMessageRepository.findByDate(LocalDate.now())
            ?: throw MykkuException(ErrorCode.NOT_FOUND_DAILY_MESSAGE)
    }
}
