package com.huntersdiary.notes.data

import com.google.cloud.Timestamp

data class FirestoreNoteModel(
    val id: String,
    val userId: String,
    val dateTime: Timestamp,
    val location: String,
    val target: String,
    val text: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
)
