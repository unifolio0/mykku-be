package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Contest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContestRepository : JpaRepository<Contest, Long>
