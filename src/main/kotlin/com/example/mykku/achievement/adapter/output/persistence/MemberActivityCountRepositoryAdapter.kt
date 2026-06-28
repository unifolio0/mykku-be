package com.example.mykku.achievement.adapter.output.persistence

import com.example.mykku.achievement.adapter.output.persistence.repository.MemberActivityCountJpaRepository
import com.example.mykku.achievement.application.port.output.MemberActivityCountRepository
import com.example.mykku.achievement.domain.vo.ActivityType
import org.springframework.stereotype.Component

@Component
class MemberActivityCountRepositoryAdapter(
    private val jpaRepository: MemberActivityCountJpaRepository
) : MemberActivityCountRepository {

    override fun incrementAndGet(memberId: Long, activityType: ActivityType): Long {
        jpaRepository.upsertIncrement(memberId, activityType.name)
        return jpaRepository.findByMemberIdAndActivityType(memberId, activityType)
            ?.count
            ?: 0L
    }
}
