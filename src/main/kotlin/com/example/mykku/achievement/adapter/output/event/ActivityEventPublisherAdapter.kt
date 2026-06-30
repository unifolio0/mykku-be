package com.example.mykku.achievement.adapter.output.event

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ActivityEventPublisherAdapter(
    private val applicationEventPublisher: ApplicationEventPublisher
) : ActivityEventPublisher {

    override fun publish(event: ActivityEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
