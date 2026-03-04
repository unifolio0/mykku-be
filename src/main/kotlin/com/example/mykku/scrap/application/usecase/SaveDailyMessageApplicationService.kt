package com.example.mykku.scrap.application.usecase

import com.example.mykku.scrap.application.dto.GetSavedDailyMessagesQuery
import com.example.mykku.scrap.application.dto.SaveDailyMessageCommand
import com.example.mykku.scrap.application.dto.SaveDailyMessageResult
import com.example.mykku.scrap.application.dto.UnsaveDailyMessageCommand
import com.example.mykku.scrap.application.port.input.SaveDailyMessageUseCase
import com.example.mykku.scrap.application.port.output.SaveDailyMessagePort
import com.example.mykku.scrap.domain.entity.SaveDailyMessageEntity
import com.example.mykku.scrap.exception.ScrapException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaveDailyMessageApplicationService(
    private val saveDailyMessagePort: SaveDailyMessagePort
) : SaveDailyMessageUseCase {

    @Transactional
    override fun saveDailyMessage(command: SaveDailyMessageCommand) {
        if (saveDailyMessagePort.existsByMemberIdAndDailyMessageId(command.memberId, command.dailyMessageId)) {
            throw ScrapException.saveDailyMessageAlreadyExists()
        }

        val saveDailyMessage = SaveDailyMessageEntity.create(
            memberId = command.memberId,
            dailyMessageId = command.dailyMessageId
        )

        saveDailyMessagePort.save(saveDailyMessage)
    }

    @Transactional
    override fun unsaveDailyMessage(command: UnsaveDailyMessageCommand) {
        if (!saveDailyMessagePort.existsByMemberIdAndDailyMessageId(command.memberId, command.dailyMessageId)) {
            throw ScrapException.saveDailyMessageNotFound()
        }
        saveDailyMessagePort.deleteByMemberIdAndDailyMessageId(command.memberId, command.dailyMessageId)
    }

    @Transactional(readOnly = true)
    override fun getSavedDailyMessages(query: GetSavedDailyMessagesQuery): Page<SaveDailyMessageResult> {
        val pageable = PageRequest.of(
            query.page,
            query.size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        return saveDailyMessagePort.findByMemberId(query.memberId, pageable)
    }

    @Transactional(readOnly = true)
    override fun isSaved(memberId: Long, dailyMessageId: Long): Boolean {
        return saveDailyMessagePort.existsByMemberIdAndDailyMessageId(memberId, dailyMessageId)
    }
}
