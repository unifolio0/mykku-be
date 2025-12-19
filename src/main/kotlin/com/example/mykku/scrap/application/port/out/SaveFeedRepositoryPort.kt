package com.example.mykku.scrap.application.port.out

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveFeed

interface SaveFeedRepositoryPort {
    fun saveFeed(member: Member, feed: Feed, folder: Folder): SaveFeed
    fun updateFolder(member: Member, feed: Feed, folder: Folder)
    fun unsaveFeed(member: Member, feed: Feed)
}
