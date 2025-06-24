package com.example.mykku.dailymessage.repository

import com.example.mykku.dailymessage.domain.DailyMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyMessageRepository : JpaRepository<DailyMessage, Long> {
    fun findByDate(date: LocalDate): DailyMessage?
}
