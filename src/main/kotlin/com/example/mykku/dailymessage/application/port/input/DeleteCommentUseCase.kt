package com.example.mykku.dailymessage.application.port.input

interface DeleteCommentUseCase {
    fun execute(commentId: Long, memberId: Long)
}
