package com.example.mykku.scrap.application.port.out

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveFanNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveFanNoteQueryPort {
    fun isSaved(member: Member, fanNote: FanNote): Boolean
    fun getSavedFanNotes(member: Member, pageable: Pageable): Page<SaveFanNote>
}
