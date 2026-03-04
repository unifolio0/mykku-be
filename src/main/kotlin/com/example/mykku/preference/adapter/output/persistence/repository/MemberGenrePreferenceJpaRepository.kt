package com.example.mykku.preference.adapter.output.persistence.repository

import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.preference.adapter.output.persistence.entity.MemberGenrePreferenceJpaEntity
import com.example.mykku.preference.domain.vo.GenreType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberGenrePreferenceJpaRepository : JpaRepository<MemberGenrePreferenceJpaEntity, Long> {
    fun findByMember(member: MemberJpaEntity): List<MemberGenrePreferenceJpaEntity>
    fun findByMemberId(memberId: Long): List<MemberGenrePreferenceJpaEntity>
    fun deleteByMember(member: MemberJpaEntity)
    fun deleteByMemberId(memberId: Long)
    fun existsByMemberAndGenreType(member: MemberJpaEntity, genreType: GenreType): Boolean
    fun existsByMemberIdAndGenreType(memberId: Long, genreType: GenreType): Boolean
}
