package com.example.mykku.dailymessage.repository

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyMessageRepository : JpaRepository<DailyMessage, Long> {
    fun findByDate(date: LocalDate): DailyMessage?

    fun getByDate(date: LocalDate): DailyMessage {
        return findByDate(date) ?: throw MykkuException(ErrorCode.NOT_FOUND_DAILY_MESSAGE)
    }
}
