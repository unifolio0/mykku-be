package com.example.mykku.member.repository

import com.example.mykku.member.domain.Follow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowRepository : JpaRepository<Follow, Long> {
    fun findByFollowerId(followerId: String): List<Follow>
}
