package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.ContestWinner
import org.springframework.data.jpa.repository.JpaRepository

interface ContestWinnerRepository : JpaRepository<ContestWinner, Long> {

}
