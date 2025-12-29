package com.example.mykku.scrap.tool

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveFanNote
import com.example.mykku.scrap.repository.SaveFanNoteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class SaveFanNoteReader(
    private val saveFanNoteRepository: SaveFanNoteRepository
) {
    fun isSaved(member: Member, fanNote: FanNote): Boolean {
        return saveFanNoteRepository.existsByMemberAndFanNote(member, fanNote)
    }

    fun getSavedFanNotes(member: Member, pageable: Pageable): Page<SaveFanNote> {
        return saveFanNoteRepository.findByMember(member, pageable)
    }
}
