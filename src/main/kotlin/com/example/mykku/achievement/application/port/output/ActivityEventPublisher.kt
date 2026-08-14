package com.example.mykku.achievement.application.port.output

import com.example.mykku.achievement.application.event.ActivityEvent

interface ActivityEventPublisher {
    fun publish(event: ActivityEvent)
}
