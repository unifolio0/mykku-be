package com.example.mykku.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(status: HttpStatus, message: String) {
    NOT_FOUND_DAILY_MESSAGE(HttpStatus.NOT_FOUND, "하루 덕담을 찾을 수 없습니다"),
}
