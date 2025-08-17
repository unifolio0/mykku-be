package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : JpaRepository<Tag, Long> {
    fun findByTitle(title: String): Tag?
    fun findAllByTitleIn(titles: Collection<String>): List<Tag>
}