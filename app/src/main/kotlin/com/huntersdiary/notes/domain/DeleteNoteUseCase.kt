package com.huntersdiary.notes.domain

import com.huntersdiary.core.error.NotFoundException

class DeleteNoteUseCase(
    private val noteRepository: NoteRepository,
) {
    suspend fun execute(userId: String, noteId: String) {
        if (!noteRepository.delete(userId, noteId)) {
            throw NotFoundException("Note not found")
        }
    }
}
