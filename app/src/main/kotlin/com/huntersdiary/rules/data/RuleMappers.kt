package com.huntersdiary.rules.data

import com.google.cloud.firestore.DocumentSnapshot
import com.huntersdiary.rules.domain.HuntingRule

fun FirestoreRuleModel.toDomain(): HuntingRule =
    HuntingRule(
        id = id,
        title = title,
        target = target,
        season = season,
        region = region,
        text = text,
    )

fun DocumentSnapshot.toFirestoreRuleModel(): FirestoreRuleModel? {
    val data = data ?: return null

    return FirestoreRuleModel(
        id = data["id"] as? String ?: id,
        title = data["title"] as? String ?: return null,
        target = data["target"] as? String ?: return null,
        season = data["season"] as? String ?: return null,
        region = data["region"] as? String ?: return null,
        text = data["text"] as? String ?: return null,
    )
}

fun FirestoreRuleModel.toFirestoreMap(): Map<String, Any> =
    mapOf(
        "id" to id,
        "title" to title,
        "target" to target,
        "season" to season,
        "region" to region,
        "text" to text,
    )
