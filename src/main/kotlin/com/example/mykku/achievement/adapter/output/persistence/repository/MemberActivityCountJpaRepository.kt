package com.example.mykku.achievement.adapter.output.persistence.repository

import com.example.mykku.achievement.adapter.output.persistence.entity.MemberActivityCountJpaEntity
import com.example.mykku.achievement.domain.vo.ActivityType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MemberActivityCountJpaRepository : JpaRepository<MemberActivityCountJpaEntity, Long> {

    @Modifying(clearAutomatically = true)
    @Query(
        value = """
            INSERT INTO member_activity_count (member_id, activity_type, count, created_at, updated_at)
            VALUES (:memberId, :activityType, 1, NOW(6), NOW(6))
            ON DUPLICATE KEY UPDATE count = count + 1, updated_at = NOW(6)
        """,
        nativeQuery = true
    )
    fun upsertIncrement(@Param("memberId") memberId: Long, @Param("activityType") activityType: String): Int

    fun findByMemberIdAndActivityType(memberId: Long, activityType: ActivityType): MemberActivityCountJpaEntity?
}
