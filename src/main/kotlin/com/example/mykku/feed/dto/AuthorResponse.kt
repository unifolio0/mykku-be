package com.example.mykku.feed.dto

import com.example.mykku.member.domain.Member

data class AuthorResponse(
    val memberId: String,
    val nickname: String,
    val profileImage: String,
    val role: String,
) {
    constructor(member: Member) : this(
        memberId = member.id,
        nickname = member.nickname,
        profileImage = member.profileImage,
        role = member.role,
    )
}
