package com.example.mykku.scrap.repository

import com.example.mykku.contest.domain.Contest
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveContest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveContestRepository : JpaRepository<SaveContest, Long> {
    fun existsByMemberAndContest(member: Member, contest: Contest): Boolean
    fun findByMember(member: Member, pageable: Pageable): Page<SaveContest>
    fun findByMemberAndContest(member: Member, contest: Contest): SaveContest?
    fun deleteByMemberAndContest(member: Member, contest: Contest)
    fun findByMemberAndContestIn(member: Member, contests: List<Contest>): List<SaveContest>
}
