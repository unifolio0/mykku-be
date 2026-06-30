package com.example.mykku.admin.service

import com.example.mykku.admin.dto.board.BoardCreateRequest
import com.example.mykku.board.application.dto.BoardResult
import com.example.mykku.board.application.dto.CreateBoardCommand
import com.example.mykku.board.application.port.input.CreateBoardUseCase
import com.example.mykku.board.application.port.input.ListBoardsUseCase
import com.example.mykku.image.ImageUploadService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminBoardService(
    private val createBoardUseCase: CreateBoardUseCase,
    private val listBoardsUseCase: ListBoardsUseCase,
    private val imageUploadService: ImageUploadService
) {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun create(request: BoardCreateRequest): BoardResult {
        val logoUrl = imageUploadService.uploadImage(request.logo).url
        val command = CreateBoardCommand(
            title = request.title,
            logo = logoUrl
        )
        return try {
            createBoardUseCase.create(command)
        } catch (e: Exception) {
            imageUploadService.delete(logoUrl)
            throw e
        }
    }

    fun findAll(): List<BoardResult> {
        return listBoardsUseCase.listBoards()
    }
}
