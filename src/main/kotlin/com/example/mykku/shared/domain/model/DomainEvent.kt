package com.example.mykku.shared.domain.model

import java.time.Instant
import java.util.UUID

abstract class DomainEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val occurredAt: Instant = Instant.now()
)