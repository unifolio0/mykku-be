package com.example.mykku.member.domain.model

import com.example.mykku.member.domain.SocialProvider
import java.time.Instant

class MemberDomain private constructor(
    val id: MemberId,
    val nickname: Nickname,
    val email: Email,
    val profileImage: String,
    val provider: SocialProvider,
    val socialId: String?,
    private var _password: Password?,
    private var _emailVerified: Boolean,
    private var _followerCount: Int,
    private var _followingCount: Int,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val password: Password? get() = _password
    val emailVerified: Boolean get() = _emailVerified
    val followerCount: Int get() = _followerCount
    val followingCount: Int get() = _followingCount
    val updatedAt: Instant get() = _updatedAt

    companion object {
        fun createEmailMember(
            id: MemberId,
            email: Email,
            password: Password,
            nickname: Nickname,
            profileImage: String = ""
        ): MemberDomain {
            val now = Instant.now()
            return MemberDomain(
                id = id,
                nickname = nickname,
                email = email,
                profileImage = profileImage,
                provider = SocialProvider.EMAIL,
                socialId = null,
                _password = password,
                _emailVerified = false,
                _followerCount = 0,
                _followingCount = 0,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun createSocialMember(
            id: MemberId,
            nickname: Nickname,
            profileImage: String,
            provider: SocialProvider,
            socialId: String,
            email: Email
        ): MemberDomain {
            val now = Instant.now()
            return MemberDomain(
                id = id,
                nickname = nickname,
                email = email,
                profileImage = profileImage,
                provider = provider,
                socialId = socialId,
                _password = null,
                _emailVerified = true,
                _followerCount = 0,
                _followingCount = 0,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: MemberId,
            nickname: Nickname,
            email: Email,
            profileImage: String,
            provider: SocialProvider,
            socialId: String?,
            password: Password?,
            emailVerified: Boolean,
            followerCount: Int,
            followingCount: Int,
            createdAt: Instant,
            updatedAt: Instant
        ): MemberDomain {
            return MemberDomain(
                id = id,
                nickname = nickname,
                email = email,
                profileImage = profileImage,
                provider = provider,
                socialId = socialId,
                _password = password,
                _emailVerified = emailVerified,
                _followerCount = followerCount,
                _followingCount = followingCount,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }

    fun changePassword(newPassword: Password) {
        _password = newPassword
        _updatedAt = Instant.now()
    }

    fun verifyEmail() {
        _emailVerified = true
        _updatedAt = Instant.now()
    }

    fun incrementFollowerCount() {
        _followerCount++
        _updatedAt = Instant.now()
    }

    fun decrementFollowerCount() {
        if (_followerCount > 0) {
            _followerCount--
            _updatedAt = Instant.now()
        }
    }

    fun incrementFollowingCount() {
        _followingCount++
        _updatedAt = Instant.now()
    }

    fun decrementFollowingCount() {
        if (_followingCount > 0) {
            _followingCount--
            _updatedAt = Instant.now()
        }
    }

    fun isEmailMember(): Boolean = provider == SocialProvider.EMAIL

    fun isSocialMember(): Boolean = provider != SocialProvider.EMAIL
}