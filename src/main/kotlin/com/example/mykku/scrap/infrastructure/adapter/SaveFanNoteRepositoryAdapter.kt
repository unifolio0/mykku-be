package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveFanNoteQueryPort
import com.example.mykku.scrap.application.port.out.SaveFanNoteRepositoryPort
import com.example.mykku.scrap.domain.SaveFanNote
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveFanNoteRepository
import org.springframework.stereotype.Component

@Component
class SaveFanNoteRepositoryAdapter(
    private val saveFanNoteRepository: SaveFanNoteRepository,
    private val saveFanNoteQueryPort: SaveFanNoteQueryPort
) : SaveFanNoteRepositoryPort {

    override fun saveFanNote(member: Member, fanNote: FanNote): SaveFanNote {
        if (saveFanNoteQueryPort.isSaved(member, fanNote)) {
            throw ScrapException.saveFanNoteAlreadyExists()
        }

        val saveFanNote = SaveFanNote(
            member = member,
            fanNote = fanNote
        )

        return saveFanNoteRepository.save(saveFanNote)
    }

    override fun unsaveFanNote(member: Member, fanNote: FanNote) {
        if (!saveFanNoteQueryPort.isSaved(member, fanNote)) {
            throw ScrapException.saveFanNoteNotFound()
        }

        saveFanNoteRepository.deleteByMemberAndFanNote(member, fanNote)
    }
}
