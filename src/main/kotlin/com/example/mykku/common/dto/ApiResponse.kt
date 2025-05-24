package com.example.mykku.common.dto

data class ApiResponse<T>(
    val message: String,
    val data: T,
)
