package com.example.mykku.fannote.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class FanNotePage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var pageNumber: Int,

    @Column(nullable = false, length = 500)
    var imageUrl: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_note_id", nullable = false)
    var fanNote: FanNote
) : BaseEntity() {

    fun updatePage(pageNumber: Int, imageUrl: String) {
        this.pageNumber = pageNumber
        this.imageUrl = imageUrl
    }
}
