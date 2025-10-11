package com.example.mykku.scrap.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.FolderRepository
import org.springframework.stereotype.Component

@Component
class FolderReader(
    private val folderRepository: FolderRepository
) {
    fun getFolderById(folderId: Long, member: Member): Folder {
        val folder = folderRepository.findByMemberAndId(member, folderId)
            ?: throw ScrapException.folderNotFound()

        if (folder.member.id != member.id) {
            throw ScrapException.folderUnauthorized()
        }

        return folder
    }

    fun getFoldersByMember(member: Member): List<Folder> {
        return folderRepository.findByMember(member)
    }

    fun existsByName(member: Member, name: String): Boolean {
        return folderRepository.existsByMemberAndName(member, name)
    }
}
