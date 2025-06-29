package com.example.mykku.dailymessage.repository

import com.example.mykku.dailymessage.domain.DailyMessageComment
import org.springframework.data.jpa.repository.JpaRepository

interface DailyMessageCommentRepository : JpaRepository<DailyMessageComment, Long>
