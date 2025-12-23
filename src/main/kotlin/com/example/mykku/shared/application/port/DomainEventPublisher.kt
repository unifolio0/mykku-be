package com.example.mykku.shared.application.port

import com.example.mykku.shared.domain.model.DomainEvent

interface DomainEventPublisher {
    fun publish(event: DomainEvent)
    fun publishAll(events: List<DomainEvent>)
}