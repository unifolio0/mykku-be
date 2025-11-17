package com.example.mykku.role.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "role")
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    var name: String,

    @Column(length = 200)
    var description: String? = null
) : BaseEntity()
