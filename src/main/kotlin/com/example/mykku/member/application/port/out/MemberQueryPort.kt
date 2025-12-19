package com.example.mykku.member.application.port.out

import com.example.mykku.member.domain.model.MemberId

data class MemberSummary(
    val id: String,
    val nickname: String,
    val profileImage: String
)

interface MemberQueryPort {
    fun findSummaryById(id: MemberId): MemberSummary?
    fun existsById(id: MemberId): Boolean
}
