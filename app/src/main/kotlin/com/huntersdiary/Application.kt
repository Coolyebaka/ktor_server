package com.huntersdiary

import com.huntersdiary.core.config.AppConfig
import com.huntersdiary.core.config.AppConfigLoader
import com.huntersdiary.core.di.coreModule
import com.huntersdiary.core.error.configureErrorHandling
import com.huntersdiary.core.routing.healthRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.logger.slf4jLogger
import org.koin.ktor.plugin.Koin

fun main() {
    val config = AppConfigLoader.load()

    embeddedServer(
        factory = Netty,
        port = config.port,
        module = { module(config) },
    ).start(wait = true)
}

fun Application.module(config: AppConfig = AppConfigLoader.load()) {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            },
        )
    }

    install(Koin) {
        slf4jLogger()
        modules(coreModule(config))
    }

    configureErrorHandling()

    routing {
        healthRoutes()
    }
}
