package com.huntersdiary.rules.data

data class FirestoreRuleModel(
    val id: String,
    val title: String,
    val target: String,
    val season: String,
    val region: String,
    val text: String,
)
