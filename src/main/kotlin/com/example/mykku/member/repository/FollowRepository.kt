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
        AND NOT EXISTS (
            SELECT 1 
            FROM Follow f3 
            WHERE f3.follower.id = :memberId
            AND f3.following.id = f2.following.id
        )
        GROUP BY f2.following 
        HAVING COUNT(DISTINCT f1.following) >= :minCommonFollowers
    """)
    fun findRecommendedMembersByCommonFollowers(
        @Param("memberId") memberId: String,
        @Param("minCommonFollowers") minCommonFollowers: Long = 10
    ): List<Member>

    @Query("""
        SELECT f2.following, COUNT(DISTINCT f1.following) as commonCount
        FROM Follow f1 
        JOIN Follow f2 ON f1.following.id = f2.follower.id 
        WHERE f1.follower.id = :memberId 
        AND f2.following.id != :memberId
        AND NOT EXISTS (
            SELECT 1 
            FROM Follow f3 
            WHERE f3.follower.id = :memberId
            AND f3.following.id = f2.following.id
        )
        GROUP BY f2.following 
        ORDER BY commonCount DESC
        LIMIT :limit
    """, nativeQuery = true)
    fun findTopRecommendedMembers(
        @Param("memberId") memberId: String,
        @Param("limit") limit: Int = 20
    ): List<Member>
}
