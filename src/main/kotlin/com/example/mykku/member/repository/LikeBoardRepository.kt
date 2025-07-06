package com.example.mykku.member.repository

import com.example.mykku.member.domain.LikeBoard
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeBoardRepository : JpaRepository<LikeBoard, Long> {
}
