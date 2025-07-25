package com.example.mykku.member.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Member(
    @Id
    val id: String,

    @Column(name = "nickname")
    var nickname: String,

    @Column(name = "role")
    var role: String,

    @Column(name = "profile_image")
    var profileImage: String,

    @Column(name = "follower_count")
    var followerCount: Int = 0,

    @Column(name = "following_count")
    var followingCount: Int = 0,
) : BaseEntity() {
    companion object {
        const val NICKNAME_MAX_LENGTH = 10
        val VALID_NICKNAME_PATTERN = Regex("^[가-힣a-zA-Z0-9]+$")
    }

    init {
        if (nickname.length > NICKNAME_MAX_LENGTH) {
            throw MykkuException(ErrorCode.MEMBER_NICKNAME_TOO_LONG)
        }
        if (!VALID_NICKNAME_PATTERN.matches(nickname)) {
            throw MykkuException(ErrorCode.MEMBER_NICKNAME_INVALID_FORMAT)
        }
    }
}
