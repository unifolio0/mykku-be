package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveDailyMessageQueryPort
import com.example.mykku.scrap.application.port.out.SaveDailyMessageRepositoryPort
import com.example.mykku.scrap.domain.SaveDailyMessage
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveDailyMessageRepository
import org.springframework.stereotype.Component

@Component
class SaveDailyMessageRepositoryAdapter(
    private val saveDailyMessageRepository: SaveDailyMessageRepository,
    private val saveDailyMessageQueryPort: SaveDailyMessageQueryPort
) : SaveDailyMessageRepositoryPort {

    override fun saveDailyMessage(member: Member, dailyMessage: DailyMessage): SaveDailyMessage {
        if (saveDailyMessageQueryPort.isSaved(member, dailyMessage)) {
            throw ScrapException.saveDailyMessageAlreadyExists()
        }

        val saveDailyMessage = SaveDailyMessage(
            member = member,
            dailyMessage = dailyMessage
        )

        return saveDailyMessageRepository.save(saveDailyMessage)
    }

    override fun unsaveDailyMessage(member: Member, dailyMessage: DailyMessage) {
        if (!saveDailyMessageQueryPort.isSaved(member, dailyMessage)) {
            throw ScrapException.saveDailyMessageNotFound()
        }

        saveDailyMessageRepository.deleteByMemberAndDailyMessage(member, dailyMessage)
    }
}
