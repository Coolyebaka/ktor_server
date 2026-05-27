package com.huntersdiary.rules.domain

import com.huntersdiary.core.error.NotFoundException

class GetRuleByIdUseCase(
    private val ruleRepository: RuleRepository,
) {
    suspend fun execute(ruleId: String): HuntingRule =
        ruleRepository.findById(ruleId)
            ?: throw NotFoundException("Rule not found")
}
