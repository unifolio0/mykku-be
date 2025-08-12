package com.example.mykku.auth.tool

interface OauthClient<T, U> {
    fun getAuthUrl(): String
    
    fun exchangeCodeForToken(code: String): T

    fun getUserInfo(token: String): U
}