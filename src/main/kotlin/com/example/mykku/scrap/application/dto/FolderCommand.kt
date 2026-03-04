package com.example.mykku.scrap.application.dto

data class CreateFolderCommand(
    val memberId: Long,
    val name: String,
    val description: String?
)

data class UpdateFolderCommand(
    val memberId: Long,
    val folderId: Long,
    val name: String,
    val description: String?
)

data class DeleteFolderCommand(
    val memberId: Long,
    val folderId: Long
)
