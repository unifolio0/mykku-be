package com.example.mykku.common.domain

import java.time.LocalDateTime

abstract class BaseEntity {
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set
}
