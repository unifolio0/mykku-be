package com.example.mykku.scrap.infrastructure.adapter

import com.example.mykku.contest.domain.Contest
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.application.port.out.SaveContestQueryPort
import com.example.mykku.scrap.application.port.out.SaveContestRepositoryPort
import com.example.mykku.scrap.domain.SaveContest
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveContestRepository
import org.springframework.stereotype.Component

@Component
class SaveContestRepositoryAdapter(
    private val saveContestRepository: SaveContestRepository,
    private val saveContestQueryPort: SaveContestQueryPort
) : SaveContestRepositoryPort {

    override fun saveContest(member: Member, contest: Contest): SaveContest {
        if (saveContestQueryPort.isSaved(member, contest)) {
            throw ScrapException.saveContestAlreadyExists()
        }

        val saveContest = SaveContest(
            member = member,
            contest = contest
        )

        return saveContestRepository.save(saveContest)
    }

    override fun unsaveContest(member: Member, contest: Contest) {
        if (!saveContestQueryPort.isSaved(member, contest)) {
            throw ScrapException.saveContestNotFound()
        }

        saveContestRepository.deleteByMemberAndContest(member, contest)
    }
}
