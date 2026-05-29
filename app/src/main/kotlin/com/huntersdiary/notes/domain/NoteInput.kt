package com.huntersdiary.notes.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class NoteInput(
    val date: LocalDate?,
    val time: LocalTime?,
    val location: String?,
    val target: String?,
    val text: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)
