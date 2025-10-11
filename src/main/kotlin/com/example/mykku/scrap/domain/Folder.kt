package com.example.mykku.scrap.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class Folder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @Column(nullable = false, length = 50)
    var name: String,

    @Column(length = 200)
    var description: String? = null
) : BaseEntity() {

    fun updateInfo(name: String, description: String?) {
        this.name = name
        this.description = description
    }
}
