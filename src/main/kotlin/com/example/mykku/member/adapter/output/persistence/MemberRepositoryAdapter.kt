package com.example.mykku.member.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberId
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryAdapter(
    private val memberJpaRepository: MemberJpaRepository,
    private val roleJpaRepository: RoleJpaRepository
) : MemberRepository {

    override fun save(member: Member): Member {
        val existingEntity = memberJpaRepository.findById(member.id.value).orElse(null)

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

    override fun findById(id: MemberId): Member? {
        return memberJpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByIdString(id: String): Member? {
        return memberJpaRepository.findById(id).orElse(null)?.toDomain()
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
}
