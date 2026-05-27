package com.huntersdiary.rules.domain

class GetRulesUseCase(
    private val ruleRepository: RuleRepository,
) {
    suspend fun execute(query: String?): List<HuntingRule> =
        ruleRepository.findAll(query?.trim()?.takeIf(String::isNotBlank))
}
