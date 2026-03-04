package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.feed.adapter.output.persistence.FeedJpaRepository
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.member.exception.MemberException
import com.example.mykku.scrap.adapter.output.persistence.entity.SaveFeedJpaEntity
import com.example.mykku.scrap.application.dto.SaveFeedResult
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import com.example.mykku.scrap.domain.entity.SaveFeedEntity
import com.example.mykku.scrap.exception.ScrapException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SaveFeedPersistenceAdapter(
    private val saveFeedJpaRepository: SaveFeedJpaRepository,
    private val folderJpaRepository: FolderJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val feedJpaRepository: FeedJpaRepository
) : SaveFeedPort {

    override fun save(saveFeed: SaveFeedEntity): SaveFeedEntity {
        val member = memberJpaRepository.findByIdOrNull(saveFeed.memberId)
            ?: throw MemberException.memberNotFound()
        val feed = feedJpaRepository.findByIdOrNull(saveFeed.feedId)
            ?: throw FeedException.feedNotFound()
        val folder = folderJpaRepository.findByIdOrNull(saveFeed.folderId)
            ?: throw ScrapException.folderNotFound()

        val jpaEntity = if (saveFeed.id != null) {
            val existing = saveFeedJpaRepository.findByIdOrNull(saveFeed.id.value)
                ?: throw ScrapException.saveFeedNotFound()
            existing.updateFolder(folder)
            existing
        } else {
            SaveFeedJpaEntity.fromDomain(saveFeed, member, feed, folder)
        }

        return saveFeedJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndFeedId(memberId: Long, feedId: Long): Boolean {
        return saveFeedJpaRepository.existsByMemberIdAndFeedId(memberId, feedId)
    }

    override fun findByMemberIdAndFeedIdIn(memberId: Long, feedIds: List<Long>): List<SaveFeedEntity> {
        return saveFeedJpaRepository.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.toDomain() }
    }

    override fun findByMemberIdAndFolderId(memberId: Long, folderId: Long?, pageable: Pageable): Page<SaveFeedResult> {
        return saveFeedJpaRepository.findByMemberIdAndFolderId(memberId, folderId, pageable)
            .map { jpaEntity ->
                SaveFeedResult(
                    id = jpaEntity.id!!,
                    feedId = jpaEntity.feed.id!!,
                    folderId = jpaEntity.folder.id!!,
                    folderName = jpaEntity.folder.name
                )
            }
    }

    override fun findByMemberIdAndFeedId(memberId: Long, feedId: Long): SaveFeedEntity? {
        return saveFeedJpaRepository.findByMemberIdAndFeedId(memberId, feedId)?.toDomain()
    }

    override fun deleteByMemberIdAndFeedId(memberId: Long, feedId: Long) {
        saveFeedJpaRepository.deleteByMemberIdAndFeedId(memberId, feedId)
    }

    override fun deleteAllByFeedId(feedId: Long) {
        saveFeedJpaRepository.deleteAllByFeedId(feedId)
    }
}
