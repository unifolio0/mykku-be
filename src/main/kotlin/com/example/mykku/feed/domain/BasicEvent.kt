package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "basic_events")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Event::class, name = "EVENT"),
    JsonSubTypes.Type(value = Contest::class, name = "CONTEST")
)
abstract class BasicEvent(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title")
    var title: String,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,

    @OneToMany(mappedBy = "basicEvent", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val basicEventImages: MutableList<BasicEventImage> = mutableListOf(),
) : BaseEntity() {
    abstract fun getType(): String
}
