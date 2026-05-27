package com.huntersdiary.notes.data

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Firestore
import com.huntersdiary.core.firestore.FirestoreProvider
import com.huntersdiary.notes.domain.Note
import com.huntersdiary.notes.domain.NoteInput
import com.huntersdiary.notes.domain.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreNoteRepository(
    private val firestoreProvider: FirestoreProvider,
) : NoteRepository {
    override suspend fun findByUserId(userId: String, query: String?): List<Note> =
        withContext(Dispatchers.IO) {
            val notes = notesCollection()
                .whereEqualTo("userId", userId)
                .get()
                .get()
                .documents
                .mapNotNull { it.toFirestoreNoteModel()?.toDomain() }
                .sortedByDescending { it.dateTime }

            query?.let { notes.filterByQuery(it) } ?: notes
        }

    override suspend fun findByIdForUser(userId: String, noteId: String): Note? =
        withContext(Dispatchers.IO) {
            notesCollection()
                .document(noteId)
                .get()
                .get()
                .toFirestoreNoteModel()
                ?.toDomain()
                ?.takeIf { it.userId == userId }
        }

    override suspend fun create(userId: String, input: NoteInput): Note =
        withContext(Dispatchers.IO) {
            val document = notesCollection().document()
            val now = Timestamp.now()
            val model = FirestoreNoteModel(
                id = document.id,
                userId = userId,
                dateTime = input.dateTime.toFirestoreTimestamp(),
                location = input.location,
                target = input.target,
                text = input.text,
                createdAt = now,
                updatedAt = now,
            )

            document.set(model.toFirestoreMap()).get()

            model.toDomain()
        }

    override suspend fun update(userId: String, noteId: String, input: NoteInput): Note? =
        withContext(Dispatchers.IO) {
            val existing = findByIdForUser(userId, noteId) ?: return@withContext null
            val model = FirestoreNoteModel(
                id = existing.id,
                userId = existing.userId,
                dateTime = input.dateTime.toFirestoreTimestamp(),
                location = input.location,
                target = input.target,
                text = input.text,
                createdAt = existing.createdAt.toFirestoreTimestamp(),
                updatedAt = Timestamp.now(),
            )

            notesCollection().document(noteId).set(model.toFirestoreMap()).get()

            model.toDomain()
        }

    override suspend fun delete(userId: String, noteId: String): Boolean =
        withContext(Dispatchers.IO) {
            findByIdForUser(userId, noteId) ?: return@withContext false

            notesCollection().document(noteId).delete().get()
            true
        }

    private fun List<Note>.filterByQuery(query: String): List<Note> {
        val normalizedQuery = query.lowercase()

        return filter { note ->
            note.location.contains(normalizedQuery, ignoreCase = true) ||
                note.target.contains(normalizedQuery, ignoreCase = true) ||
                note.text.contains(normalizedQuery, ignoreCase = true) ||
                note.dateTime.toString().lowercase().contains(normalizedQuery)
        }
    }

    private fun notesCollection() =
        firestore().collection(NOTES_COLLECTION)

    private fun firestore(): Firestore =
        firestoreProvider.get()

    private companion object {
        const val NOTES_COLLECTION = "notes"
    }
}
