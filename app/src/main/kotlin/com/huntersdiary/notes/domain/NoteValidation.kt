package com.huntersdiary.notes.domain

import com.huntersdiary.core.error.ValidationException

fun NoteInput.validated(): NoteInput {
    if (location.isBlank()) {
        throw ValidationException("Location must not be blank")
    }
    if (target.isBlank()) {
        throw ValidationException("Target must not be blank")
    }
    if (text.isBlank()) {
        throw ValidationException("Text must not be blank")
    }

    return copy(
        location = location.trim(),
        target = target.trim(),
        text = text.trim(),
    )
}
