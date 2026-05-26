package com.huntersdiary.core.security

import com.huntersdiary.core.config.JwtConfig
import com.huntersdiary.core.error.ApiError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

fun Application.configureSecurity(
    jwtConfig: JwtConfig,
    jwtService: JwtService,
) {
    if (jwtConfig.secret.isNullOrBlank()) {
        environment.log.warn("JWT authentication is not installed because JWT_SECRET is not configured")
        return
    }

    install(Authentication) {
        jwt(JwtService.AUTH_PROVIDER) {
            verifier(jwtService.verifier())
            validate { credential ->
                val userId = credential.payload.getClaim(JwtService.USER_ID_CLAIM).asString()

                if (userId.isNullOrBlank()) {
                    null
                } else {
                    JWTPrincipal(credential.payload)
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiError("UNAUTHORIZED", "Unauthorized"),
                )
            }
        }
    }
}
