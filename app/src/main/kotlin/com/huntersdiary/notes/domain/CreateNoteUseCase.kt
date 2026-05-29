package com.huntersdiary.notes.domain

class CreateNoteUseCase(
    private val noteRepository: NoteRepository,
) {
    suspend fun execute(userId: String, input: NoteInput): Note =
        noteRepository.create(userId, input.validated())
}
