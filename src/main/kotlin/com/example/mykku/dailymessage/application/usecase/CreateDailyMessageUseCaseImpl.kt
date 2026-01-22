package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.dto.CreateDailyMessageCommand
import com.example.mykku.dailymessage.application.dto.DailyMessageSummaryResult
import com.example.mykku.dailymessage.application.port.input.CreateDailyMessageUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import com.example.mykku.dailymessage.domain.entity.DailyMessage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateDailyMessageUseCaseImpl(
    private val dailyMessageRepository: DailyMessageRepository
) : CreateDailyMessageUseCase {

    override fun execute(command: CreateDailyMessageCommand): DailyMessageSummaryResult {
        val dailyMessage = DailyMessage.create(
            title = command.title,
            content = command.content,
            date = command.date
        )

        val saved = dailyMessageRepository.save(dailyMessage)
        return DailyMessageSummaryResult.from(saved)
    }
}
