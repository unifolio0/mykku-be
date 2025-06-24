package com.example.mykku.board.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import jakarta.persistence.*

@Entity
class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feeds: MutableList<Feed> = mutableListOf(),
) : BaseEntity() {
    companion object {
        const val TITLE_MAX_LENGTH = 16
    }

    init {
        if (title.length > TITLE_MAX_LENGTH) {
            throw MykkuException(ErrorCode.BOARD_TITLE_TOO_LONG)
        }
    }
}
