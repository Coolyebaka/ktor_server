package com.huntersdiary

import com.huntersdiary.auth.di.authModule
import com.huntersdiary.auth.domain.LoginUseCase
import com.huntersdiary.auth.domain.RegisterUseCase
import com.huntersdiary.auth.presentation.authRoutes
import com.huntersdiary.core.config.AppConfig
import com.huntersdiary.core.config.AppConfigLoader
import com.huntersdiary.core.di.coreModule
import com.huntersdiary.core.error.configureErrorHandling
import com.huntersdiary.core.routing.healthRoutes
import com.huntersdiary.core.security.JwtService
import com.huntersdiary.core.security.configureSecurity
import com.huntersdiary.notes.di.notesModule
import com.huntersdiary.notes.domain.CreateNoteUseCase
import com.huntersdiary.notes.domain.DeleteNoteUseCase
import com.huntersdiary.notes.domain.GetNoteByIdUseCase
import com.huntersdiary.notes.domain.GetNotesUseCase
import com.huntersdiary.notes.domain.UpdateNoteUseCase
import com.huntersdiary.notes.presentation.noteRoutes
import com.huntersdiary.rules.di.rulesModule
import com.huntersdiary.rules.domain.GetRuleByIdUseCase
import com.huntersdiary.rules.domain.GetRulesUseCase
import com.huntersdiary.rules.presentation.ruleRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.logger.slf4jLogger
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    val config = AppConfigLoader.load()

    embeddedServer(
        factory = Netty,
        host = config.host,
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
        modules(
            coreModule(config),
            authModule,
            notesModule,
            rulesModule,
        )
    }

    configureErrorHandling()

    val jwtService by inject<JwtService>()
    configureSecurity(config.jwt, jwtService)

    val registerUseCase by inject<RegisterUseCase>()
    val loginUseCase by inject<LoginUseCase>()
    val createNoteUseCase by inject<CreateNoteUseCase>()
    val getNotesUseCase by inject<GetNotesUseCase>()
    val getNoteByIdUseCase by inject<GetNoteByIdUseCase>()
    val updateNoteUseCase by inject<UpdateNoteUseCase>()
    val deleteNoteUseCase by inject<DeleteNoteUseCase>()
    val getRulesUseCase by inject<GetRulesUseCase>()
    val getRuleByIdUseCase by inject<GetRuleByIdUseCase>()

    routing {
        healthRoutes()
        authRoutes(registerUseCase, loginUseCase)
        noteRoutes(
            createNoteUseCase = createNoteUseCase,
            getNotesUseCase = getNotesUseCase,
            getNoteByIdUseCase = getNoteByIdUseCase,
            updateNoteUseCase = updateNoteUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
        )
        ruleRoutes(
            getRulesUseCase = getRulesUseCase,
            getRuleByIdUseCase = getRuleByIdUseCase,
        )
    }
}
