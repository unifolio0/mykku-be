package com.example.mykku.member.application.port.output

import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberId

interface MemberRepository {
    fun save(member: Member): Member
    fun findById(id: MemberId): Member?
    fun findByIdString(id: String): Member?
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Member?
    fun existsByMemberId(memberId: String): Boolean
    fun findByMemberId(memberId: String): Member?
}
