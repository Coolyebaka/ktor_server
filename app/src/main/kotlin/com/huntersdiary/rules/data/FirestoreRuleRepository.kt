package com.huntersdiary.rules.data

import com.google.cloud.firestore.Firestore
import com.huntersdiary.core.firestore.FirestoreProvider
import com.huntersdiary.rules.domain.HuntingRule
import com.huntersdiary.rules.domain.RuleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreRuleRepository(
    private val firestoreProvider: FirestoreProvider,
) : RuleRepository {
    override suspend fun findAll(query: String?): List<HuntingRule> =
        withContext(Dispatchers.IO) {
            ensureSeeded()

            val rules = rulesCollection()
                .get()
                .get()
                .documents
                .mapNotNull { it.toFirestoreRuleModel()?.toDomain() }
                .sortedBy { it.title }

            query?.let { rules.filterByQuery(it) } ?: rules
        }

    override suspend fun findById(ruleId: String): HuntingRule? =
        withContext(Dispatchers.IO) {
            ensureSeeded()

            rulesCollection()
                .document(ruleId)
                .get()
                .get()
                .toFirestoreRuleModel()
                ?.toDomain()
        }

    private fun ensureSeeded() {
        val hasAnyRule = !rulesCollection()
            .limit(1)
            .get()
            .get()
            .isEmpty

        if (hasAnyRule) {
            return
        }

        val batch = firestore().batch()
        ruleSeedData.forEach { rule ->
            batch.set(rulesCollection().document(rule.id), rule.toFirestoreMap())
        }
        batch.commit().get()
    }

    private fun List<HuntingRule>.filterByQuery(query: String): List<HuntingRule> {
        val normalizedQuery = query.lowercase()

        return filter { rule ->
            rule.title.contains(normalizedQuery, ignoreCase = true) ||
                rule.target.contains(normalizedQuery, ignoreCase = true) ||
                rule.season.contains(normalizedQuery, ignoreCase = true) ||
                rule.region.contains(normalizedQuery, ignoreCase = true) ||
                rule.text.contains(normalizedQuery, ignoreCase = true)
        }
    }

    private fun rulesCollection() =
        firestore().collection(RULES_COLLECTION)

    private fun firestore(): Firestore =
        firestoreProvider.get()

    private companion object {
        const val RULES_COLLECTION = "rules"
    }
}
