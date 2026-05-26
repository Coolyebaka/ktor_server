package com.huntersdiary.core.di

import com.huntersdiary.core.config.AppConfig
import com.huntersdiary.core.firestore.FirestoreProvider
import com.huntersdiary.core.security.JwtSecurity
import org.koin.dsl.module

fun coreModule(config: AppConfig) = module {
    single { config }
    single { config.jwt }
    single { config.firestore }
    single { JwtSecurity(get()) }
    single { FirestoreProvider(get()) }
}
