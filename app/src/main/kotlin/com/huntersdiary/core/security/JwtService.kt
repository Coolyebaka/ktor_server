package com.huntersdiary.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.huntersdiary.core.config.JwtConfig
import java.time.Instant
import java.util.Date

class JwtService(
    private val config: JwtConfig,
) {
    fun createToken(userId: String): String {
        val now = Instant.now()

        return JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim(USER_ID_CLAIM, userId)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(config.tokenTtlSeconds)))
            .sign(algorithm())
    }

    fun verifier(): JWTVerifier =
        JWT.require(algorithm())
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()

    private fun algorithm(): Algorithm {
        val secret = requireNotNull(config.secret) {
            "JWT_SECRET is required to create or verify JWT tokens"
        }

        return Algorithm.HMAC256(secret)
    }

    companion object {
        const val AUTH_PROVIDER = "auth-jwt"
        const val USER_ID_CLAIM = "userId"
    }
}
