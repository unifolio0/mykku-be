package com.example.mykku.achievement.application.event

import com.example.mykku.achievement.application.port.input.AwardTitlesUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ActivityEventListener(
    private val awardTitlesUseCase: AwardTitlesUseCase
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Async("titleAwardExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleActivityEvent(event: ActivityEvent) {
        try {
            awardTitlesUseCase.handleActivity(event.memberId, event.activityType)
        } catch (e: Exception) {
            log.error("칭호 부여 처리 실패: memberId={}, activityType={}", event.memberId, event.activityType, e)
        }
    }
}
