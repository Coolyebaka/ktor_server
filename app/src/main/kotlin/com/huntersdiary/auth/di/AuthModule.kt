package com.huntersdiary.auth.di

import com.huntersdiary.auth.data.FirestoreAuthRepository
import com.huntersdiary.auth.domain.AuthRepository
import com.huntersdiary.auth.domain.LoginUseCase
import com.huntersdiary.auth.domain.RegisterUseCase
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { FirestoreAuthRepository(get()) }
    single { RegisterUseCase(get(), get(), get()) }
    single { LoginUseCase(get(), get(), get()) }
}
