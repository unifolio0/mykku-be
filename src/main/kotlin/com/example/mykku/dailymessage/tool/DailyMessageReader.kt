package com.example.mykku.dailymessage.tool

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DailyMessageReader(
    private val dailyMessageRepository: DailyMessageRepository,
) {
    fun getTodayDailyMessage(): DailyMessage {
        return dailyMessageRepository.findByDate(LocalDate.now())
            ?: throw MykkuException(ErrorCode.DAILY_MESSAGE_NOT_FOUND)
    }

    fun getDailyMessages(date: LocalDate, limit: Int, sort: SortDirection): List<DailyMessage> {
        val pageable = PageRequest.of(0, limit)

        return when (sort) {
            SortDirection.ASC -> dailyMessageRepository.findByDateBeforeOrEqualOrderByDateAsc(date, pageable)
            SortDirection.DESC -> dailyMessageRepository.findByDateBeforeOrEqualOrderByDateDesc(date, pageable)
        }
    }

    fun getDailyMessage(id: Long): DailyMessage {
        return dailyMessageRepository.findById(id)
            .orElseThrow { MykkuException(ErrorCode.DAILY_MESSAGE_NOT_FOUND) }
    }
}
