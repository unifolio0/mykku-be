package com.example.mykku.scrap.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder

interface FolderRepositoryPort {
    fun createFolder(member: Member, name: String, description: String?): Folder
    fun updateFolder(folderId: Long, member: Member, name: String, description: String?): Folder
    fun deleteFolder(folderId: Long, member: Member)
}
