package com.huntersdiary.notes.domain

interface NoteRepository {
    suspend fun findByUserId(userId: String, query: String?): List<Note>

    suspend fun findByIdForUser(userId: String, noteId: String): Note?

    suspend fun create(userId: String, input: NoteInput): Note

    suspend fun update(userId: String, noteId: String, input: NoteInput): Note?

    suspend fun delete(userId: String, noteId: String): Boolean
}
