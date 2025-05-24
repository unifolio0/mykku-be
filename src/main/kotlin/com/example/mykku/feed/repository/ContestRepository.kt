package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Contest
import org.springframework.data.jpa.repository.JpaRepository

interface ContestRepository : JpaRepository<Contest, Long> {

}
