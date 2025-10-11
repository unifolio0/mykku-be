package com.example.mykku.scrap.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FolderRepository : JpaRepository<Folder, Long> {
    fun findByMemberAndId(member: Member, id: Long): Folder?
    fun findByMember(member: Member): List<Folder>
    fun existsByMemberAndName(member: Member, name: String): Boolean
}
