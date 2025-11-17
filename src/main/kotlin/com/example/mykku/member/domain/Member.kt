package com.example.mykku.member.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.domain.Role
import jakarta.persistence.*

@Entity
class Member(
    @Id
    val id: String,

    @Column(name = "nickname")
    var nickname: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,

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
    var emailVerified: Boolean = false,

    @Column(name = "follower_count")
    var followerCount: Int = 0,

    @Column(name = "following_count")
    var followingCount: Int = 0,
) : BaseEntity() {
    companion object {
        const val NICKNAME_MAX_LENGTH = 10
        val VALID_NICKNAME_PATTERN = Regex("^[가-힣a-zA-Z0-9\\s]+$")

        fun createEmailMember(
            id: String,
            email: String,
            password: String,
            nickname: String,
            defaultRole: Role,
            profileImage: String = ""
        ): Member {
            return Member(
                id = id,
                nickname = nickname,
                role = defaultRole,
                profileImage = profileImage,
                provider = SocialProvider.EMAIL,
                socialId = null,
                email = email,
                password = password,
                emailVerified = false
            )
        }

        fun createSocialMember(
            id: String,
            nickname: String,
            profileImage: String,
            provider: SocialProvider,
            socialId: String,
            email: String,
            defaultRole: Role
        ): Member {
            return Member(
                id = id,
                nickname = nickname,
                role = defaultRole,
                profileImage = profileImage,
                provider = provider,
                socialId = socialId,
                email = email,
                password = null,
                emailVerified = true
            )
        }
    }

    init {
        if (nickname.length > NICKNAME_MAX_LENGTH) {
            throw MemberException.memberNicknameTooLong()
        }
        if (!VALID_NICKNAME_PATTERN.matches(nickname)) {
            throw MemberException.memberNicknameInvalidFormat()
        }
    }
}
