package com.example.mykku.feed.domain

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("TAG")
class BasicTag(
    title: String
) : Tag(
    title = title
) {
    override fun getTagType(): String = "TAG"
}
