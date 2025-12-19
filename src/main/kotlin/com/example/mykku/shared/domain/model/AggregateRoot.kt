package com.example.mykku.shared.domain.model

import java.time.Instant

abstract class AggregateRoot<ID>(
    val id: ID,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    private val domainEvents: MutableList<DomainEvent> = mutableListOf()

    protected fun registerEvent(event: DomainEvent) {
        domainEvents.add(event)
    }

    fun pullDomainEvents(): List<DomainEvent> {
        val events = domainEvents.toList()
        domainEvents.clear()
        return events
    }

    fun hasDomainEvents(): Boolean = domainEvents.isNotEmpty()
}