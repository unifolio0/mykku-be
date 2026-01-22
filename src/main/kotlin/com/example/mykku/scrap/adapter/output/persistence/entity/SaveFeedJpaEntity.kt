package com.example.mykku.scrap.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.scrap.domain.entity.SaveFeedEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "save_feed")
class SaveFeedJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: FeedJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    var folder: FolderJpaEntity
) : BaseEntity() {

    fun toDomain(): SaveFeedEntity {
        return SaveFeedEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            feedId = this.feed.id!!,
            folderId = this.folder.id!!,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    fun updateFolder(folder: FolderJpaEntity) {
        this.folder = folder
    }

    companion object {
        fun fromDomain(
            domain: SaveFeedEntity,
            member: MemberJpaEntity,
            feed: FeedJpaEntity,
            folder: FolderJpaEntity
        ): SaveFeedJpaEntity {
            return SaveFeedJpaEntity(
                id = domain.id?.value,
                member = member,
                feed = feed,
                folder = folder
            )
        }
    }
}
