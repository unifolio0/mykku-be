package com.example.mykku.scrap.application.port.input

import com.example.mykku.scrap.application.dto.CreateFolderCommand
import com.example.mykku.scrap.application.dto.DeleteFolderCommand
import com.example.mykku.scrap.application.dto.FolderResult
import com.example.mykku.scrap.application.dto.FoldersResult
import com.example.mykku.scrap.application.dto.UpdateFolderCommand

interface FolderUseCase {
    fun createFolder(command: CreateFolderCommand): FolderResult
    fun getFolders(memberId: String): FoldersResult
    fun updateFolder(command: UpdateFolderCommand): FolderResult
    fun deleteFolder(command: DeleteFolderCommand)
}
