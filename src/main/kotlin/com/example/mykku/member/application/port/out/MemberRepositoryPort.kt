package com.example.mykku.member.application.port.out

import com.example.mykku.member.domain.model.Email
import com.example.mykku.member.domain.model.MemberDomain
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.member.domain.model.Nickname

interface MemberRepositoryPort {
    fun save(member: MemberDomain): MemberDomain
    fun findById(id: MemberId): MemberDomain?
    fun findByEmail(email: Email): MemberDomain?
    fun existsById(id: MemberId): Boolean
    fun existsByEmail(email: Email): Boolean
    fun existsByNickname(nickname: Nickname): Boolean
}