package com.example.mykku.scrap.tool

import com.example.mykku.contest.domain.Contest
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveContest
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveContestRepository
import org.springframework.stereotype.Component

@Component
class SaveContestWriter(
    private val saveContestRepository: SaveContestRepository,
    private val saveContestReader: SaveContestReader
) {
    fun saveContest(member: Member, contest: Contest): SaveContest {
        if (saveContestReader.isSaved(member, contest)) {
            throw ScrapException.saveContestAlreadyExists()
        }

        val saveContest = SaveContest(
            member = member,
            contest = contest
        )

        return saveContestRepository.save(saveContest)
    }

    fun unsaveContest(member: Member, contest: Contest) {
        if (!saveContestReader.isSaved(member, contest)) {
            throw ScrapException.saveContestNotFound()
        }

        saveContestRepository.deleteByMemberAndContest(member, contest)
    }
}
