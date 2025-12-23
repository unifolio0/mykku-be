package com.example.mykku.scrap.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder

interface FolderQueryPort {
    fun getFolderById(folderId: Long, member: Member): Folder
    fun getFoldersByMember(member: Member): List<Folder>
    fun existsByName(member: Member, name: String): Boolean
}
