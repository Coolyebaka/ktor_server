package com.huntersdiary.notes.data

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
                .sortedWith(
                    compareByDescending<Note> { it.date?.toString() ?: "" }
                        .thenByDescending { it.time?.toString() ?: "" },
                )

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
            val createdAt = requireNotNull(input.createdAt).toFirestoreTimestamp()
            val updatedAt = requireNotNull(input.updatedAt).toFirestoreTimestamp()
            val model = FirestoreNoteModel(
                id = document.id,
                userId = userId,
                date = input.date?.toString(),
                time = input.time?.toString(),
                location = input.location,
                target = input.target,
                text = input.text,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )

            document.set(model.toFirestoreMap()).get()

            model.toDomain()
        }

    override suspend fun update(userId: String, noteId: String, input: NoteInput): Note? =
        withContext(Dispatchers.IO) {
            val existing = findByIdForUser(userId, noteId) ?: return@withContext null
            val createdAt = requireNotNull(input.createdAt).toFirestoreTimestamp()
            val updatedAt = requireNotNull(input.updatedAt).toFirestoreTimestamp()
            val model = FirestoreNoteModel(
                id = existing.id,
                userId = existing.userId,
                date = input.date?.toString(),
                time = input.time?.toString(),
                location = input.location,
                target = input.target,
                text = input.text,
                createdAt = createdAt,
                updatedAt = updatedAt,
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
                note.location?.contains(normalizedQuery, ignoreCase = true) == true ||
                note.target?.contains(normalizedQuery, ignoreCase = true) == true ||
                note.text?.contains(normalizedQuery, ignoreCase = true) == true ||
                note.date?.toString()?.lowercase()?.contains(normalizedQuery) == true ||
                note.time?.toString()?.lowercase()?.contains(normalizedQuery) == true
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
