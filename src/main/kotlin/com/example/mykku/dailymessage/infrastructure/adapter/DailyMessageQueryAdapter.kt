package com.example.mykku.dailymessage.infrastructure.adapter

import com.example.mykku.dailymessage.application.port.out.DailyMessageQueryPort
import com.example.mykku.dailymessage.application.port.out.DailyMessageSummary
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.domain.model.DailyMessageId
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DailyMessageQueryAdapter(
    private val dailyMessageRepository: DailyMessageRepository,
    private val dailyMessageCommentRepository: DailyMessageCommentRepository
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

    override fun getTodayDailyMessage(): DailyMessage {
        return dailyMessageRepository.findByDate(LocalDate.now())
            ?: throw DailyMessageException.dailyMessageNotFound()
    }

    override fun getDailyMessages(date: LocalDate, limit: Int, sort: SortDirection): List<DailyMessage> {
        val pageable = PageRequest.of(0, limit)

        return when (sort) {
            SortDirection.ASC -> dailyMessageRepository.findByDateBeforeOrEqualOrderByDateAsc(date, pageable)
            SortDirection.DESC -> dailyMessageRepository.findByDateBeforeOrEqualOrderByDateDesc(date, pageable)
        }
    }

    override fun getDailyMessage(id: Long): DailyMessage {
        return dailyMessageRepository.findById(id)
            .orElseThrow { DailyMessageException.dailyMessageNotFound() }
    }

    override fun findAll(pageable: Pageable): Page<DailyMessage> {
        return dailyMessageRepository.findAll(pageable)
    }

    override fun getCommentsByDailyMessage(dailyMessage: DailyMessage): List<DailyMessageComment> {
        return dailyMessageCommentRepository.findByDailyMessage(dailyMessage)
    }
}
