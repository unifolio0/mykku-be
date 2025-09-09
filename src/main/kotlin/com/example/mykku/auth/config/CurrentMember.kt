package com.example.mykku.auth.config

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentMember(
    val required: Boolean = true
)
