package com.huntersdiary.core.di

import com.huntersdiary.core.config.AppConfig
import com.huntersdiary.core.firestore.FirestoreProvider
import com.huntersdiary.core.security.JwtService
import com.huntersdiary.core.security.PasswordHasher
import org.koin.dsl.module

fun coreModule(config: AppConfig) = module {
    single { config }
    single { config.jwt }
    single { config.firestore }
    single { JwtService(get()) }
    single { PasswordHasher() }
    single { FirestoreProvider(get()) }
}
