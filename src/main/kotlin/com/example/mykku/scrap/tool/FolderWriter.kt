package com.example.mykku.scrap.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.FolderRepository
import org.springframework.stereotype.Component

@Component
class FolderWriter(
    private val folderRepository: FolderRepository,
    private val folderReader: FolderReader
) {
    fun createFolder(member: Member, name: String, description: String?): Folder {
        validateFolderName(member, name)

        val folder = Folder(
            member = member,
            name = name,
            description = description
        )

        return folderRepository.save(folder)
    }

    fun updateFolder(folderId: Long, member: Member, name: String, description: String?): Folder {
        val folder = folderReader.getFolderById(folderId, member)

        if (folder.name != name) {
            validateFolderName(member, name)
        }

        folder.updateInfo(name, description)
        return folderRepository.save(folder)
    }

    fun deleteFolder(folderId: Long, member: Member) {
        val folder = folderReader.getFolderById(folderId, member)
        folderRepository.delete(folder)
    }

    private fun validateFolderName(member: Member, name: String) {
        if (name.length > 50) {
            throw ScrapException.folderNameTooLong()
        }

        if (folderReader.existsByName(member, name)) {
            throw ScrapException.folderNameDuplicate()
        }
    }
}
