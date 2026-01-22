package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.port.input.DeleteDailyMessageUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import com.example.mykku.dailymessage.exception.DailyMessageException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteDailyMessageUseCaseImpl(
    private val dailyMessageRepository: DailyMessageRepository
) : DeleteDailyMessageUseCase {

    override fun execute(id: Long) {
        val dailyMessageId = DailyMessageId.of(id)

        dailyMessageRepository.findById(dailyMessageId)
            ?: throw DailyMessageException.dailyMessageNotFound()

        dailyMessageRepository.deleteById(dailyMessageId)
    }
}
