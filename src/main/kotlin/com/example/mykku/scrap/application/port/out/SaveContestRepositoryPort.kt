package com.example.mykku.scrap.application.port.out

import com.example.mykku.contest.domain.Contest
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveContest

interface SaveContestRepositoryPort {
    fun saveContest(member: Member, contest: Contest): SaveContest
    fun unsaveContest(member: Member, contest: Contest)
}
