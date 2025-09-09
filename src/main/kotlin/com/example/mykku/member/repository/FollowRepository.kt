package com.example.mykku.member.repository

import com.example.mykku.member.domain.Follow
import com.example.mykku.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FollowRepository : JpaRepository<Follow, Long> {
    fun findByFollowerId(followerId: String): List<Follow>
    
    @Query("""
        SELECT DISTINCT f2.following 
        FROM Follow f1 
        JOIN Follow f2 ON f1.following.id = f2.follower.id 
        WHERE f1.follower.id = :memberId 
        AND f2.following.id NOT IN (
            SELECT f3.following.id 
            FROM Follow f3 
            WHERE f3.follower.id = :memberId
        )
        GROUP BY f2.following 
        HAVING COUNT(DISTINCT f1.following) >= :minCommonFollowers
    """)
    fun findRecommendedMembersByCommonFollowers(
        @Param("memberId") memberId: String,
        @Param("minCommonFollowers") minCommonFollowers: Long = 10
    ): List<Member>
}
