package com.example.mykku.member.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.domain.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Member(
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
    var emailVerified: Boolean = false,
) : BaseEntity() {
    companion object {
        const val NICKNAME_MAX_LENGTH = 10
        val VALID_NICKNAME_PATTERN = Regex("^[가-힣a-zA-Z0-9\\s]+$")

        const val MEMBER_ID_MAX_LENGTH = 16
        val VALID_MEMBER_ID_PATTERN = Regex("^[a-zA-Z0-9]+$")

        fun validateMemberId(memberId: String) {
            if (memberId.isBlank()) {
                throw MemberException.memberIdEmpty()
            }
            if (memberId.length > MEMBER_ID_MAX_LENGTH) {
                throw MemberException.memberIdTooLong()
            }
            if (!VALID_MEMBER_ID_PATTERN.matches(memberId)) {
                throw MemberException.memberIdInvalidFormat()
            }
        }

        fun createEmailMember(
            id: String,
            memberId: String,
            email: String,
            password: String,
            nickname: String,
            profileImage: String = ""
        ): Member {
            validateMemberId(memberId)
            return Member(
                id = id,
                memberId = memberId,
                nickname = nickname,
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
            memberId: String,
            nickname: String,
            profileImage: String,
            provider: SocialProvider,
            socialId: String,
            email: String
        ): Member {
            validateMemberId(memberId)
            return Member(
                id = id,
                memberId = memberId,
                nickname = nickname,
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
        validateNickname(nickname)
    }

    fun updateProfile(newNickname: String?, newProfileImage: String?) {
        newNickname?.let {
            validateNickname(it)
            this.nickname = it
        }
        newProfileImage?.let {
            this.profileImage = it
        }
    }

    private fun validateNickname(nickname: String) {
        if (nickname.length > NICKNAME_MAX_LENGTH) {
            throw MemberException.memberNicknameTooLong()
        }
        if (!VALID_NICKNAME_PATTERN.matches(nickname)) {
            throw MemberException.memberNicknameInvalidFormat()
        }
    }
}
