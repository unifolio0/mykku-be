package com.example.mykku.member.infrastructure.persistence.adapter

import com.example.mykku.member.application.port.out.MemberRepositoryPort
import com.example.mykku.member.domain.model.*
import com.example.mykku.member.infrastructure.persistence.mapper.MemberMapper
import com.example.mykku.member.repository.MemberRepository
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryAdapter(
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper
) : MemberRepositoryPort {

    override fun save(member: MemberDomain): MemberDomain {
        val existingEntity = memberRepository.findById(member.id.value).orElse(null)

        val entityToSave = if (existingEntity != null) {
            memberMapper.updateEntity(existingEntity, member)
        } else {
            memberMapper.toEntity(member)
        }

        val savedEntity = memberRepository.save(entityToSave)
        return memberMapper.toDomain(savedEntity)
    }

    override fun findById(id: MemberId): MemberDomain? {
        return memberRepository.findById(id.value)
            .map { memberMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByEmail(email: Email): MemberDomain? {
        return memberRepository.findByEmail(email.value)
            ?.let { memberMapper.toDomain(it) }
    }

    override fun existsById(id: MemberId): Boolean {
        return memberRepository.existsById(id.value)
    }

    override fun existsByEmail(email: Email): Boolean {
        return memberRepository.existsByEmail(email.value)
    }

    override fun existsByNickname(nickname: Nickname): Boolean {
        return memberRepository.existsByNickname(nickname.value)
    }
}
