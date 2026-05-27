package com.huntersdiary.notes.domain

import kotlinx.datetime.Instant

data class NoteInput(
    val dateTime: Instant,
    val location: String,
    val target: String,
    val text: String,
)
