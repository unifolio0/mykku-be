package com.example.mykku.scrap

import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.dto.CreateFolderRequest
import com.example.mykku.scrap.dto.FolderResponse
import com.example.mykku.scrap.dto.FoldersResponse
import com.example.mykku.scrap.dto.UpdateFolderRequest
import com.example.mykku.scrap.tool.FolderReader
import com.example.mykku.scrap.tool.FolderWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FolderService(
    private val folderReader: FolderReader,
    private val folderWriter: FolderWriter
) {
    @Transactional
    fun createFolder(request: CreateFolderRequest, member: Member): FolderResponse {
        val folder = folderWriter.createFolder(member, request.name, request.description)
        return FolderResponse.from(folder)
    }

    @Transactional(readOnly = true)
    fun getFolders(member: Member): FoldersResponse {
        val folders = folderReader.getFoldersByMember(member)
        return FoldersResponse(
            folders = folders.map { FolderResponse.from(it) }
        )
    }

    @Transactional
    fun updateFolder(folderId: Long, request: UpdateFolderRequest, member: Member): FolderResponse {
        val folder = folderWriter.updateFolder(folderId, member, request.name, request.description)
        return FolderResponse.from(folder)
    }

    @Transactional
    fun deleteFolder(folderId: Long, member: Member) {
        folderWriter.deleteFolder(folderId, member)
    }
}
