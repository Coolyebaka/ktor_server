package com.huntersdiary.auth.domain

interface AuthRepository {
    suspend fun findByEmail(email: String): User?

    suspend fun createUser(email: String, passwordHash: String): User
}
