package com.example.mykku.scrap.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.entity.FolderEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "folder")
class FolderJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @Column(nullable = false, length = 50)
    var name: String,

    @Column(length = 200)
    var description: String? = null
) : BaseEntity() {

    fun toDomain(): FolderEntity {
        return FolderEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            name = this.name,
            description = this.description,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    fun updateInfo(name: String, description: String?) {
        this.name = name
        this.description = description
    }

    companion object {
        fun fromDomain(
            domain: FolderEntity,
            member: Member
        ): FolderJpaEntity {
            return FolderJpaEntity(
                id = domain.id?.value,
                member = member,
                name = domain.name,
                description = domain.description
            )
        }
    }
}
