package com.example.mykku.member.repository

import com.example.mykku.member.domain.LikeFeed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeFeedRepository : JpaRepository<LikeFeed, Long>
