package com.example.mykku.member.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.SocialProvider
import com.example.mykku.role.domain.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class MemberJpaEntity(
    @Id
    val id: String,

    @Column(name = "member_id", unique = true, nullable = false, length = 16)
    val memberId: String,

    @Column(name = "nickname")
    var nickname: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    var role: Role? = null,

    @Column(name = "profile_image")
    var profileImage: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    val provider: SocialProvider?,

    @Column(name = "social_id")
    val socialId: String?,

    @Column(name = "email")
    val email: String,

    @Column(name = "password")
    var password: String? = null,

    @Column(name = "email_verified")
    var emailVerified: Boolean = false
) : BaseEntity() {

    fun toDomain(): Member {
        return Member.reconstitute(
            id = this.id,
            memberId = this.memberId,
            nickname = this.nickname,
            roleId = this.role?.id,
            profileImage = this.profileImage,
            provider = this.provider,
            socialId = this.socialId,
            email = this.email,
            password = this.password,
            emailVerified = this.emailVerified,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    fun updateFromDomain(member: Member) {
        this.nickname = member.nickname
        this.profileImage = member.profileImage
        this.password = member.password
        this.emailVerified = member.emailVerified
    }

    companion object {
        fun fromDomain(member: Member, role: Role? = null): MemberJpaEntity {
            return MemberJpaEntity(
                id = member.id.value,
                memberId = member.memberId,
                nickname = member.nickname,
                role = role,
                profileImage = member.profileImage,
                provider = member.provider,
                socialId = member.socialId,
                email = member.email,
                password = member.password,
                emailVerified = member.emailVerified
            )
        }
    }
}
