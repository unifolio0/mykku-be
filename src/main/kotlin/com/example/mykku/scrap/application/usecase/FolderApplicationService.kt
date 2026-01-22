package com.example.mykku.scrap.application.usecase

import com.example.mykku.scrap.application.dto.CreateFolderCommand
import com.example.mykku.scrap.application.dto.DeleteFolderCommand
import com.example.mykku.scrap.application.dto.FolderResult
import com.example.mykku.scrap.application.dto.FoldersResult
import com.example.mykku.scrap.application.dto.UpdateFolderCommand
import com.example.mykku.scrap.application.port.input.FolderUseCase
import com.example.mykku.scrap.application.port.output.FolderPort
import com.example.mykku.scrap.domain.entity.FolderEntity
import com.example.mykku.scrap.exception.ScrapException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FolderApplicationService(
    private val folderPort: FolderPort
) : FolderUseCase {

    @Transactional
    override fun createFolder(command: CreateFolderCommand): FolderResult {
        validateFolderName(command.memberId, command.name)

        val folder = FolderEntity.create(
            memberId = command.memberId,
            name = command.name,
            description = command.description
        )

        val saved = folderPort.save(folder)
        return toResult(saved)
    }

    @Transactional(readOnly = true)
    override fun getFolders(memberId: String): FoldersResult {
        val folders = folderPort.findByMemberId(memberId)
        return FoldersResult(
            folders = folders.map { toResult(it) }
        )
    }

    @Transactional
    override fun updateFolder(command: UpdateFolderCommand): FolderResult {
        val folder = folderPort.findByMemberIdAndId(command.memberId, command.folderId)
            ?: throw ScrapException.folderNotFound()

        if (folder.name != command.name) {
            validateFolderName(command.memberId, command.name)
        }

        folder.updateInfo(command.name, command.description)
        val saved = folderPort.save(folder)
        return toResult(saved)
    }

    @Transactional
    override fun deleteFolder(command: DeleteFolderCommand) {
        val folder = folderPort.findByMemberIdAndId(command.memberId, command.folderId)
            ?: throw ScrapException.folderNotFound()
        folderPort.delete(folder)
    }

    private fun validateFolderName(memberId: String, name: String) {
        if (name.length > 50) {
            throw ScrapException.folderNameTooLong()
        }
        if (folderPort.existsByMemberIdAndName(memberId, name)) {
            throw ScrapException.folderNameDuplicate()
        }
    }

    private fun toResult(folder: FolderEntity): FolderResult {
        return FolderResult(
            id = folder.id!!.value,
            name = folder.name,
            description = folder.description,
            createdAt = folder.createdAt,
            updatedAt = folder.updatedAt
        )
    }
}
