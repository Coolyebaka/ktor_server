package com.huntersdiary.notes.domain

import com.huntersdiary.core.error.NotFoundException

class UpdateNoteUseCase(
    private val noteRepository: NoteRepository,
) {
    suspend fun execute(userId: String, noteId: String, input: NoteInput): Note =
        noteRepository.update(userId, noteId, input.validated())
            ?: throw NotFoundException("Note not found")
}
