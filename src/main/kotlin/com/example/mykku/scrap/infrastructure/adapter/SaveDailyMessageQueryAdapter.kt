package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveDailyMessageQueryPort
import com.example.mykku.scrap.domain.SaveDailyMessage
import com.example.mykku.scrap.repository.SaveDailyMessageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class SaveDailyMessageQueryAdapter(
    private val saveDailyMessageRepository: SaveDailyMessageRepository
) : SaveDailyMessageQueryPort {

    override fun isSaved(member: Member, dailyMessage: DailyMessage): Boolean {
        return saveDailyMessageRepository.existsByMemberAndDailyMessage(member, dailyMessage)
    }

    override fun getSavedDailyMessages(member: Member, pageable: Pageable): Page<SaveDailyMessage> {
        return saveDailyMessageRepository.findByMember(member, pageable)
    }
}
