package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*

@Entity
@Table(name = "tags")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = BasicTag::class, name = "TAG"),
    JsonSubTypes.Type(value = EventTag::class, name = "EVENT_TAG"),
    JsonSubTypes.Type(value = ContestTag::class, name = "CONTEST_TAG")
)
abstract class Tag(
    @Id
    val id: Long? = null,

    @Column(name = "title")
    var title: String,
) : BaseEntity() {
    abstract fun getType(): String
}
