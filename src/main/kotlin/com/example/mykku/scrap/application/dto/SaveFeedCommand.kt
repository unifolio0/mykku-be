package com.example.mykku.scrap.application.dto

data class SaveFeedCommand(
    val memberId: String,
    val feedId: Long,
    val folderId: Long
)

data class UnsaveFeedCommand(
    val memberId: String,
    val feedId: Long
)

data class UpdateSaveFeedFolderCommand(
    val memberId: String,
    val feedId: Long,
    val folderId: Long
)

data class GetSavedFeedsQuery(
    val memberId: String,
    val folderId: Long?,
    val page: Int,
    val size: Int
)
