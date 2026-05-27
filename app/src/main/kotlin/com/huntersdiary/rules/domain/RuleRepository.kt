package com.huntersdiary.rules.domain

interface RuleRepository {
    suspend fun findAll(query: String?): List<HuntingRule>

    suspend fun findById(ruleId: String): HuntingRule?
}
