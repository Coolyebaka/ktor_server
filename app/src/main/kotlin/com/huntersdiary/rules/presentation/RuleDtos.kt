package com.huntersdiary.rules.presentation

import com.huntersdiary.rules.domain.HuntingRule
import kotlinx.serialization.Serializable

@Serializable
data class RuleResponse(
    val id: String,
    val title: String,
    val target: String,
    val season: String,
    val region: String,
    val text: String,
)

fun HuntingRule.toResponse(): RuleResponse =
    RuleResponse(
        id = id,
        title = title,
        target = target,
        season = season,
        region = region,
        text = text,
    )
