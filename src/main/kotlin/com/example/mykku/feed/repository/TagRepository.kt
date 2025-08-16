package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface TagRepository : JpaRepository<Tag, Long> {
    fun findByTitle(title: String): Optional<Tag>
}