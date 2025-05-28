package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
@DiscriminatorValue("CONTEST_TAG")
class ContestTag(
    title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    val contest: Contest,
) : Tag(
    title = title
) {
    override fun getTagType(): String = "CONTEST_TAG"
}
