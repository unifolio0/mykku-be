package com.example.mykku.preference.adapter.output.persistence.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.adapter.output.persistence.entity.MemberGenrePreferenceJpaEntity
import com.example.mykku.preference.domain.vo.GenreType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberGenrePreferenceJpaRepository : JpaRepository<MemberGenrePreferenceJpaEntity, Long> {
    fun findByMember(member: Member): List<MemberGenrePreferenceJpaEntity>
    fun findByMemberId(memberId: String): List<MemberGenrePreferenceJpaEntity>
    fun deleteByMember(member: Member)
    fun deleteByMemberId(memberId: String)
    fun existsByMemberAndGenreType(member: Member, genreType: GenreType): Boolean
    fun existsByMemberIdAndGenreType(memberId: String, genreType: GenreType): Boolean
}
