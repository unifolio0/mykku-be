package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.scrap.adapter.output.persistence.entity.SaveFeedJpaEntity
import com.example.mykku.scrap.application.dto.SaveFeedResult
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import com.example.mykku.scrap.domain.entity.SaveFeedEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SaveFeedPersistenceAdapter(
    private val saveFeedJpaRepository: SaveFeedJpaRepository,
    private val folderJpaRepository: FolderJpaRepository,
    private val memberRepository: MemberRepository,
    private val feedRepository: FeedRepository
) : SaveFeedPort {

    override fun save(saveFeed: SaveFeedEntity): SaveFeedEntity {
        val member = memberRepository.findByIdOrNull(saveFeed.memberId)
            ?: throw IllegalArgumentException("Member not found: ${saveFeed.memberId}")
        val feed = feedRepository.findByIdOrNull(saveFeed.feedId)
            ?: throw IllegalArgumentException("Feed not found: ${saveFeed.feedId}")
        val folder = folderJpaRepository.findByIdOrNull(saveFeed.folderId)
            ?: throw IllegalArgumentException("Folder not found: ${saveFeed.folderId}")

        val jpaEntity = if (saveFeed.id != null) {
            val existing = saveFeedJpaRepository.findByIdOrNull(saveFeed.id.value)
                ?: throw IllegalArgumentException("SaveFeed not found: ${saveFeed.id.value}")
            existing.updateFolder(folder)
            existing
        } else {
            SaveFeedJpaEntity.fromDomain(saveFeed, member, feed, folder)
        }

        return saveFeedJpaRepository.save(jpaEntity).toDomain()
    }

    override fun existsByMemberIdAndFeedId(memberId: String, feedId: Long): Boolean {
        return saveFeedJpaRepository.existsByMemberIdAndFeedId(memberId, feedId)
    }

    override fun findByMemberIdAndFeedIdIn(memberId: String, feedIds: List<Long>): List<SaveFeedEntity> {
        return saveFeedJpaRepository.findByMemberIdAndFeedIdIn(memberId, feedIds)
            .map { it.toDomain() }
    }

    override fun findByMemberIdAndFolderId(memberId: String, folderId: Long?, pageable: Pageable): Page<SaveFeedResult> {
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

    override fun findByMemberIdAndFeedId(memberId: String, feedId: Long): SaveFeedEntity? {
        return saveFeedJpaRepository.findByMemberIdAndFeedId(memberId, feedId)?.toDomain()
    }

    override fun deleteByMemberIdAndFeedId(memberId: String, feedId: Long) {
        saveFeedJpaRepository.deleteByMemberIdAndFeedId(memberId, feedId)
    }

    override fun deleteAllByFeedId(feedId: Long) {
        saveFeedJpaRepository.deleteAllByFeedId(feedId)
    }
}
