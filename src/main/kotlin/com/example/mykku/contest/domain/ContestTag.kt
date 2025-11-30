package com.example.mykku.contest.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.contest.exception.ContestException
import jakarta.persistence.*

@Entity
@Table(name = "contest_tag")
class ContestTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: Contest,
) : BaseEntity() {
    companion object {
        const val TITLE_MAX_LENGTH = 20
        val VALID_PATTERN = Regex("^[가-힣a-zA-Z0-9]+$")
    }

    init {
        if (title.length > TITLE_MAX_LENGTH) {
            throw ContestException.tagTitleTooLong()
        }
        if (!VALID_PATTERN.matches(title)) {
            throw ContestException.tagInvalidFormat()
        }
    }
}
