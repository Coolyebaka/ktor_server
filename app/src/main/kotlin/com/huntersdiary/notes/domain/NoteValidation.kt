package com.huntersdiary.notes.domain

import com.huntersdiary.core.error.ValidationException

fun NoteInput.validated(): NoteInput {
    if (createdAt == null) {
        throw ValidationException("createdAt is required")
    }
    if (updatedAt == null) {
        throw ValidationException("updatedAt is required")
    }

    return copy(
        location = location?.trim()?.takeIf(String::isNotEmpty),
        target = target?.trim()?.takeIf(String::isNotEmpty),
        text = text?.trim()?.takeIf(String::isNotEmpty),
    )
}
