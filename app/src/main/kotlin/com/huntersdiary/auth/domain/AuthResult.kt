package com.huntersdiary.auth.domain

data class AuthResult(
    val token: String,
    val user: User,
)
