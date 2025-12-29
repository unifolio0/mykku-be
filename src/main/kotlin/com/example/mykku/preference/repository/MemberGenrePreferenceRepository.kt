package com.example.mykku.preference.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.preference.domain.GenreType
import com.example.mykku.preference.domain.MemberGenrePreference
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberGenrePreferenceRepository : JpaRepository<MemberGenrePreference, Long> {
    fun findByMember(member: Member): List<MemberGenrePreference>
    fun deleteByMember(member: Member)
    fun existsByMemberAndGenreType(member: Member, genreType: GenreType): Boolean
}
