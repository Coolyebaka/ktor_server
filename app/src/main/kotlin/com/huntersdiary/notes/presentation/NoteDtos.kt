package com.huntersdiary.notes.presentation

import com.huntersdiary.notes.domain.Note
import com.huntersdiary.notes.domain.NoteInput
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val dateTime: Instant,
    val location: String,
    val target: String,
    val text: String,
)

@Serializable
data class UpdateNoteRequest(
    val dateTime: Instant,
    val location: String,
    val target: String,
    val text: String,
)

@Serializable
data class NoteResponse(
    val id: String,
    val dateTime: Instant,
    val location: String,
    val target: String,
    val text: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

fun CreateNoteRequest.toInput(): NoteInput =
    NoteInput(
        dateTime = dateTime,
        location = location,
        target = target,
        text = text,
    )

fun UpdateNoteRequest.toInput(): NoteInput =
    NoteInput(
        dateTime = dateTime,
        location = location,
        target = target,
        text = text,
    )

fun Note.toResponse(): NoteResponse =
    NoteResponse(
        id = id,
        dateTime = dateTime,
        location = location,
        target = target,
        text = text,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
