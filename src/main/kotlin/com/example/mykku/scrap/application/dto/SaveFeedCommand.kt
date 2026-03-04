package com.example.mykku.scrap.application.dto

data class SaveFeedCommand(
    val memberId: Long,
    val feedId: Long,
    val folderId: Long
)

data class UnsaveFeedCommand(
    val memberId: Long,
    val feedId: Long
)

data class UpdateSaveFeedFolderCommand(
    val memberId: Long,
    val feedId: Long,
    val folderId: Long
)

data class GetSavedFeedsQuery(
    val memberId: Long,
    val folderId: Long?,
    val page: Int,
    val size: Int
)
