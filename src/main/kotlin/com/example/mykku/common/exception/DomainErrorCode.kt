package com.example.mykku.common.exception

import org.springframework.http.HttpStatus

interface DomainErrorCode {
    val code: String
    val status: HttpStatus
    val message: String

    val name: String
}