package com.example.mykku.fannote.application.usecase

import com.example.mykku.fannote.application.port.input.DeleteFanNoteUseCase
import com.example.mykku.fannote.application.port.output.FanNotePageRepository
import com.example.mykku.fannote.application.port.output.FanNoteRepository
import com.example.mykku.fannote.domain.vo.FanNoteId
import com.example.mykku.fannote.exception.FanNoteException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteFanNoteUseCaseImpl(
    private val fanNoteRepository: FanNoteRepository,
    private val fanNotePageRepository: FanNotePageRepository
) : DeleteFanNoteUseCase {

    override fun execute(fanNoteId: Long) {
        val id = FanNoteId.of(fanNoteId)

        if (!fanNoteRepository.existsById(id)) {
            throw FanNoteException.fanNoteNotFound()
        }

        fanNotePageRepository.deleteByFanNoteId(id)
        fanNoteRepository.deleteById(id)
    }
}
