package com.huntersdiary.notes.domain

import kotlinx.datetime.Instant

data class Note(
    val id: String,
    val userId: String,
    val dateTime: Instant,
    val location: String,
    val target: String,
    val text: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
