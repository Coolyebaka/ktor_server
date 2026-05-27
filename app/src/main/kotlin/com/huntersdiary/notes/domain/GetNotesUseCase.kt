package com.huntersdiary.notes.domain

class GetNotesUseCase(
    private val noteRepository: NoteRepository,
) {
    suspend fun execute(userId: String, query: String?): List<Note> =
        noteRepository.findByUserId(userId, query?.trim()?.takeIf(String::isNotBlank))
}
