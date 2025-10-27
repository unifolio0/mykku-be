package com.example.mykku.common.exception

import org.springframework.http.HttpStatus

interface DomainErrorCode {
    val status: HttpStatus
    val message: String
    val code: String
        get() = name

    val name: String
}