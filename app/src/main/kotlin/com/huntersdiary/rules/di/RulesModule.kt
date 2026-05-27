package com.huntersdiary.rules.di

import com.huntersdiary.rules.data.FirestoreRuleRepository
import com.huntersdiary.rules.domain.GetRuleByIdUseCase
import com.huntersdiary.rules.domain.GetRulesUseCase
import com.huntersdiary.rules.domain.RuleRepository
import org.koin.dsl.module

val rulesModule = module {
    single<RuleRepository> { FirestoreRuleRepository(get()) }
    single { GetRulesUseCase(get()) }
    single { GetRuleByIdUseCase(get()) }
}
