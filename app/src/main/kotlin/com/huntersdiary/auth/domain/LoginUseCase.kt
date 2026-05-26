package com.huntersdiary.auth.domain

import com.huntersdiary.core.error.UnauthorizedException
import com.huntersdiary.core.error.ValidationException
import com.huntersdiary.core.security.JwtService
import com.huntersdiary.core.security.PasswordHasher

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtService: JwtService,
) {
    suspend fun execute(email: String, password: String): AuthResult {
        val normalizedEmail = normalizeAndValidateEmail(email)
        val user = authRepository.findByEmail(normalizedEmail)
            ?: throw UnauthorizedException("Invalid email or password")

        if (!passwordHasher.verify(password, user.passwordHash)) {
            throw UnauthorizedException("Invalid email or password")
        }

        return AuthResult(
            token = jwtService.createToken(user.id),
            user = user,
        )
    }

    private fun normalizeAndValidateEmail(email: String): String {
        val normalized = email.trim().lowercase()

        if (!EMAIL_REGEX.matches(normalized)) {
            throw ValidationException("Invalid email")
        }

        return normalized
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}
