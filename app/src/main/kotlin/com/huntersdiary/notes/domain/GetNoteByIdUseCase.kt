package com.huntersdiary.notes.domain

import com.huntersdiary.core.error.NotFoundException

class GetNoteByIdUseCase(
    private val noteRepository: NoteRepository,
) {
    suspend fun execute(userId: String, noteId: String): Note =
        noteRepository.findByIdForUser(userId, noteId)
            ?: throw NotFoundException("Note not found")
}
