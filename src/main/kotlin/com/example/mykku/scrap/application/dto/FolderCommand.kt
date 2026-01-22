package com.example.mykku.scrap.application.dto

data class CreateFolderCommand(
    val memberId: String,
    val name: String,
    val description: String?
)

data class UpdateFolderCommand(
    val memberId: String,
    val folderId: Long,
    val name: String,
    val description: String?
)

data class DeleteFolderCommand(
    val memberId: String,
    val folderId: Long
)
