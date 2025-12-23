package com.example.mykku.dailymessage.application.port.out

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.domain.model.DailyMessageId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

data class DailyMessageSummary(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDate
)

interface DailyMessageQueryPort {
    fun findSummaryById(id: DailyMessageId): DailyMessageSummary?
    fun existsById(id: DailyMessageId): Boolean

    // Cross-domain methods (returns JPA Entity for other domains)
    fun getTodayDailyMessage(): DailyMessage
    fun getDailyMessages(date: LocalDate, limit: Int, sort: SortDirection): List<DailyMessage>
    fun getDailyMessage(id: Long): DailyMessage
    fun findAll(pageable: Pageable): Page<DailyMessage>
    fun getCommentsByDailyMessage(dailyMessage: DailyMessage): List<DailyMessageComment>
}
