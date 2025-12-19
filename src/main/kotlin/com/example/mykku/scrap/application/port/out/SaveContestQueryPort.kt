package com.example.mykku.scrap.application.port.out

import com.example.mykku.contest.domain.Contest
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveContest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveContestQueryPort {
    fun isSaved(member: Member, contest: Contest): Boolean
    fun getSavedContests(member: Member, pageable: Pageable): Page<SaveContest>
    fun getSavedContestIds(member: Member, contests: List<Contest>): Set<Long>
}
