package com.example.mykku.auth.dto

data class MemberInfo(
    val memberId: String,
    val email: String,
    val nickname: String,
    val profileImage: String?
)
