package com.example.mykku.scrap.application.port.output

import com.example.mykku.scrap.application.dto.SaveFeedResult
import com.example.mykku.scrap.domain.entity.SaveFeedEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveFeedPort {
    fun save(saveFeed: SaveFeedEntity): SaveFeedEntity
    fun existsByMemberIdAndFeedId(memberId: String, feedId: Long): Boolean
    fun findByMemberIdAndFeedIdIn(memberId: String, feedIds: List<Long>): List<SaveFeedEntity>
    fun findByMemberIdAndFolderId(memberId: String, folderId: Long?, pageable: Pageable): Page<SaveFeedResult>
    fun findByMemberIdAndFeedId(memberId: String, feedId: Long): SaveFeedEntity?
    fun deleteByMemberIdAndFeedId(memberId: String, feedId: Long)
    fun deleteAllByFeedId(feedId: Long)
}
