package com.example.mykku.fannote.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "fan_notes")
class FanNote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = true, length = 200)
    var subtitle: String? = null,

    @Column(columnDefinition = "TEXT")
    var content: String? = null,

    @Column(nullable = false)
    var productionDate: LocalDate,

    @Column(nullable = true, length = 500)
    var coverImageUrl: String? = null,

    @OneToMany(mappedBy = "fanNote", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pages: MutableList<FanNotePage> = mutableListOf()
) : BaseEntity() {

    fun addPage(page: FanNotePage) {
        pages.add(page)
        page.fanNote = this
    }

    fun updateInfo(
        title: String,
        subtitle: String?,
        content: String?,
        productionDate: LocalDate,
        coverImageUrl: String?
    ) {
        this.title = title
        this.subtitle = subtitle
        this.content = content
        this.productionDate = productionDate
        this.coverImageUrl = coverImageUrl
    }
}
