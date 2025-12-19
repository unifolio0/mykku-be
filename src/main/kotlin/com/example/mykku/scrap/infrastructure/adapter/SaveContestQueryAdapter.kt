package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.contest.domain.Contest
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveContestQueryPort
import com.example.mykku.scrap.domain.SaveContest
import com.example.mykku.scrap.repository.SaveContestRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class SaveContestQueryAdapter(
    private val saveContestRepository: SaveContestRepository
) : SaveContestQueryPort {

    override fun isSaved(member: Member, contest: Contest): Boolean {
        return saveContestRepository.existsByMemberAndContest(member, contest)
    }

    override fun getSavedContests(member: Member, pageable: Pageable): Page<SaveContest> {
        return saveContestRepository.findByMember(member, pageable)
    }

    override fun getSavedContestIds(member: Member, contests: List<Contest>): Set<Long> {
        return saveContestRepository.findByMemberAndContestIn(member, contests)
            .mapNotNull { it.contest.id }
            .toSet()
    }
}
