package com.example.mykku.common.exception

data class ErrorCodeInfo(
    val code: String,
    val domain: String,
    val name: String,
    val status: Int,
    val message: String
)
