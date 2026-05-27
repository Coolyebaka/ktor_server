package com.huntersdiary.rules.domain

import com.huntersdiary.core.error.NotFoundException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class RuleUseCaseTest {
    @Test
    fun `rules can be listed searched and opened by id`() = runBlocking {
        val repository = InMemoryRuleRepository()
        val getRulesUseCase = GetRulesUseCase(repository)
        val getRuleByIdUseCase = GetRuleByIdUseCase(repository)

        assertEquals(2, getRulesUseCase.execute(null).size)
        assertEquals("duck", getRulesUseCase.execute("утка").single().id)
        assertEquals("boar", getRuleByIdUseCase.execute("boar").id)
    }

    @Test
    fun `missing rule returns not found`() {
        val getRuleByIdUseCase = GetRuleByIdUseCase(InMemoryRuleRepository())

        assertThrows(NotFoundException::class.java) {
            runBlocking {
                getRuleByIdUseCase.execute("missing")
            }
        }
    }

    private class InMemoryRuleRepository : RuleRepository {
        private val rules = listOf(
            HuntingRule(
                id = "duck",
                title = "Охота на утку",
                target = "Утка",
                season = "Осень",
                region = "Московская область",
                text = "Тестовое правило",
            ),
            HuntingRule(
                id = "boar",
                title = "Охота на кабана",
                target = "Кабан",
                season = "Зима",
                region = "Российская Федерация",
                text = "Тестовое правило",
            ),
        )

        override suspend fun findAll(query: String?): List<HuntingRule> {
            val normalizedQuery = query?.lowercase()

            return if (normalizedQuery == null) {
                rules
            } else {
                rules.filter { rule ->
                    rule.title.lowercase().contains(normalizedQuery) ||
                        rule.target.lowercase().contains(normalizedQuery) ||
                        rule.season.lowercase().contains(normalizedQuery) ||
                        rule.region.lowercase().contains(normalizedQuery) ||
                        rule.text.lowercase().contains(normalizedQuery)
                }
            }
        }

        override suspend fun findById(ruleId: String): HuntingRule? =
            rules.firstOrNull { it.id == ruleId }
    }
}
