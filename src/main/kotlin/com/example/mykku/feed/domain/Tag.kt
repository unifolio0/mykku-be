package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,
) : BaseEntity() {
    companion object {
        const val TITLE_MAX_LENGTH = 20
        val VALID_PATTERN = Regex("^[가-힣a-zA-Z0-9]+$")
    }

    init {
        if (title.length > TITLE_MAX_LENGTH) {
            throw MykkuException(ErrorCode.TAG_TITLE_TOO_LONG)
        }
        if (!VALID_PATTERN.matches(title)) {
            throw MykkuException(ErrorCode.TAG_INVALID_FORMAT)
        }
    }

    abstract fun getType(): String
}
