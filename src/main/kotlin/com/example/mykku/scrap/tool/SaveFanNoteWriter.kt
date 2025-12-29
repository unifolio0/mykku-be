package com.example.mykku.scrap.tool

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveFanNote
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveFanNoteRepository
import org.springframework.stereotype.Component

@Component
class SaveFanNoteWriter(
    private val saveFanNoteRepository: SaveFanNoteRepository,
    private val saveFanNoteReader: SaveFanNoteReader
) {
    fun saveFanNote(member: Member, fanNote: FanNote): SaveFanNote {
        if (saveFanNoteReader.isSaved(member, fanNote)) {
            throw ScrapException.saveFanNoteAlreadyExists()
        }

        val saveFanNote = SaveFanNote(
            member = member,
            fanNote = fanNote
        )

        return saveFanNoteRepository.save(saveFanNote)
    }

    fun unsaveFanNote(member: Member, fanNote: FanNote) {
        if (!saveFanNoteReader.isSaved(member, fanNote)) {
            throw ScrapException.saveFanNoteNotFound()
        }

        saveFanNoteRepository.deleteByMemberAndFanNote(member, fanNote)
    }
}
