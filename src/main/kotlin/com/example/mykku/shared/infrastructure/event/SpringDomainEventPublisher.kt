package com.example.mykku.shared.infrastructure.event

import com.example.mykku.shared.application.port.DomainEventPublisher
import com.example.mykku.shared.domain.model.DomainEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringDomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : DomainEventPublisher {

    override fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    override fun publishAll(events: List<DomainEvent>) {
        events.forEach { publish(it) }
    }
}