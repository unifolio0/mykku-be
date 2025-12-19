package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.FolderQueryPort
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.FolderRepository
import org.springframework.stereotype.Component

@Component
class FolderQueryAdapter(
    private val folderRepository: FolderRepository
) : FolderQueryPort {

    override fun getFolderById(folderId: Long, member: Member): Folder {
        return folderRepository.findByMemberAndId(member, folderId)
            ?: throw ScrapException.folderNotFound()
    }

    override fun getFoldersByMember(member: Member): List<Folder> {
        return folderRepository.findByMember(member)
    }

    override fun existsByName(member: Member, name: String): Boolean {
        return folderRepository.existsByMemberAndName(member, name)
    }
}
