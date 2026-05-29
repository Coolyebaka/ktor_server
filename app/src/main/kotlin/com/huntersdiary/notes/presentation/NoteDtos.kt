package com.huntersdiary.notes.presentation

import com.huntersdiary.notes.domain.Note
import com.huntersdiary.notes.domain.NoteInput
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val location: String? = null,
    val target: String? = null,
    val text: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)

@Serializable
data class UpdateNoteRequest(
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val location: String? = null,
    val target: String? = null,
    val text: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)

@Serializable
data class NoteResponse(
    val id: String,
    val date: LocalDate?,
    val time: LocalTime?,
    val location: String?,
    val target: String?,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

fun CreateNoteRequest.toInput(): NoteInput =
    NoteInput(
        date = date,
        time = time,
        location = location,
        target = target,
        text = text,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun UpdateNoteRequest.toInput(): NoteInput =
    NoteInput(
        date = date,
        time = time,
        location = location,
        target = target,
        text = text,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Note.toResponse(): NoteResponse =
    NoteResponse(
        id = id,
        date = date,
        time = time,
        location = location,
        target = target,
        text = text,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
