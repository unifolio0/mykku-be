package com.example.mykku.achievement.application.event

import com.example.mykku.achievement.application.port.input.AwardTitlesUseCase
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ActivityEventListener(
    private val awardTitlesUseCase: AwardTitlesUseCase
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleActivityEvent(event: ActivityEvent) {
        awardTitlesUseCase.handleActivity(event.memberId, event.activityType)
    }
}
