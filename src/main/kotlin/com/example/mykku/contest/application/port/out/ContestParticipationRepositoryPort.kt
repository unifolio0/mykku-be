package com.example.mykku.contest.application.port.out

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member

interface ContestParticipationRepositoryPort {
    fun participateViaFeed(member: Member, contest: Contest, feed: Feed): ContestParticipation
}
