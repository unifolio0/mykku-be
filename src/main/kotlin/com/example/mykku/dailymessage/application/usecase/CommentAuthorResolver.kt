package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.dto.CommentAuthorInfo
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.RoleId
import org.springframework.stereotype.Component

@Component
class CommentAuthorResolver(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository
) {
    fun resolve(memberIds: List<Long>): Map<Long, CommentAuthorInfo> {
        if (memberIds.isEmpty()) return emptyMap()

        val members = memberRepository.findByIds(memberIds.distinct().map { MemberPk.of(it) })
        val roleIds = members.mapNotNull { it.roleId }.distinct().map { RoleId.of(it) }
        val rolesById = roleRepository.findByIds(roleIds).associateBy { it.id.value }

        return members.associate { member ->
            val role = member.roleId?.let { rolesById[it] }
            member.id.value to CommentAuthorInfo(
                memberId = member.memberId,
                role = role?.let { RoleResult(it.id.value, it.name, it.description) }
            )
        }
    }

    fun resolveOne(memberId: Long?): CommentAuthorInfo? {
        return memberId?.let { resolve(listOf(it))[it] }
    }
}
