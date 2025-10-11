package com.example.mykku.scrap.tool

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveDailyMessage
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveDailyMessageRepository
import org.springframework.stereotype.Component

@Component
class SaveDailyMessageWriter(
    private val saveDailyMessageRepository: SaveDailyMessageRepository,
    private val saveDailyMessageReader: SaveDailyMessageReader
) {
    fun saveDailyMessage(member: Member, dailyMessage: DailyMessage): SaveDailyMessage {
        if (saveDailyMessageReader.isSaved(member, dailyMessage)) {
            throw ScrapException.saveDailyMessageAlreadyExists()
        }

        val saveDailyMessage = SaveDailyMessage(
            member = member,
            dailyMessage = dailyMessage
        )

        return saveDailyMessageRepository.save(saveDailyMessage)
    }

    fun unsaveDailyMessage(member: Member, dailyMessage: DailyMessage) {
        if (!saveDailyMessageReader.isSaved(member, dailyMessage)) {
            throw ScrapException.saveDailyMessageNotFound()
        }

        saveDailyMessageRepository.deleteByMemberAndDailyMessage(member, dailyMessage)
    }
}
