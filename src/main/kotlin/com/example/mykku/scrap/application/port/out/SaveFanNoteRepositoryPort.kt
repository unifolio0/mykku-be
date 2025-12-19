package com.example.mykku.scrap.application.port.out

import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveFanNote

interface SaveFanNoteRepositoryPort {
    fun saveFanNote(member: Member, fanNote: FanNote): SaveFanNote
    fun unsaveFanNote(member: Member, fanNote: FanNote)
}
