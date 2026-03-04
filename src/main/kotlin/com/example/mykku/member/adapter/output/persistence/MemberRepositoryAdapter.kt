package com.example.mykku.member.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.member.domain.vo.SocialProvider
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryAdapter(
    private val memberJpaRepository: MemberJpaRepository,
    private val roleJpaRepository: RoleJpaRepository
) : MemberRepository {

    override fun save(member: Member): Member {
        val existingEntity = if (member.id.value != 0L) memberJpaRepository.findById(member.id.value).orElse(null) else null

        val role = member.roleId?.let { roleJpaRepository.findByIdOrNull(it) }

        val entityToSave = if (existingEntity != null) {
            existingEntity.updateFromDomain(member)
            if (role != null) {
                existingEntity.role = role
            }
            existingEntity
        } else {
            MemberJpaEntity.fromDomain(member, role)
        }

        return memberJpaRepository.save(entityToSave).toDomain()
    }

    override fun findById(id: MemberPk): Member? {
        return memberJpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByProviderAndSocialId(provider: SocialProvider, socialId: String): Member? {
        return memberJpaRepository.findByProviderAndSocialId(provider, socialId)?.toDomain()
    }

    override fun existsByNickname(nickname: String): Boolean {
        return memberJpaRepository.existsByNickname(nickname)
    }

    override fun existsByEmail(email: String): Boolean {
        return memberJpaRepository.existsByEmail(email)
    }

    override fun findByEmail(email: String): Member? {
        return memberJpaRepository.findByEmail(email)?.toDomain()
    }

    override fun existsByMemberId(memberId: String): Boolean {
        return memberJpaRepository.existsByMemberId(memberId)
    }

    override fun findByMemberId(memberId: String): Member? {
        return memberJpaRepository.findByMemberId(memberId)?.toDomain()
    }

    override fun deleteById(id: MemberPk) {
        memberJpaRepository.deleteById(id.value)
    }
}
