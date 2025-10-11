package com.example.mykku.scrap.dto

import com.example.mykku.scrap.domain.Folder
import java.time.LocalDateTime

data class CreateFolderRequest(
    val name: String,
    val description: String? = null
)

data class UpdateFolderRequest(
    val name: String,
    val description: String? = null
)

data class FolderResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(folder: Folder): FolderResponse {
            return FolderResponse(
                id = folder.id!!,
                name = folder.name,
                description = folder.description,
                createdAt = folder.createdAt,
                updatedAt = folder.updatedAt
            )
        }
    }
}

data class FoldersResponse(
    val folders: List<FolderResponse>
)
