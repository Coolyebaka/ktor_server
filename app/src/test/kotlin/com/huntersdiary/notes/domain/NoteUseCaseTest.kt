package com.huntersdiary.notes.domain

import com.huntersdiary.core.error.NotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class NoteUseCaseTest {
    @Test
    fun `user can only read own notes`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val getNotesUseCase = GetNotesUseCase(repository)

        createNoteUseCase.execute("user-1", sampleInput(location = "Forest"))
        createNoteUseCase.execute("user-2", sampleInput(location = "Lake"))

        val userNotes = getNotesUseCase.execute("user-1", null)

        assertEquals(1, userNotes.size)
        assertEquals("user-1", userNotes.single().userId)
        assertEquals("Forest", userNotes.single().location)
    }

    @Test
    fun `user cannot update or delete another user note`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val updateNoteUseCase = UpdateNoteUseCase(repository)
        val deleteNoteUseCase = DeleteNoteUseCase(repository)
        val note = createNoteUseCase.execute("owner", sampleInput())

        assertThrows(NotFoundException::class.java) {
            runBlocking {
                updateNoteUseCase.execute("stranger", note.id, sampleInput(location = "Updated"))
            }
        }
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                deleteNoteUseCase.execute("stranger", note.id)
            }
        }

        assertEquals(note, repository.findByIdForUser("owner", note.id))
    }

    @Test
    fun `search checks location target text and date string`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val getNotesUseCase = GetNotesUseCase(repository)

        createNoteUseCase.execute("user-1", sampleInput(location = "Northern forest"))
        createNoteUseCase.execute("user-1", sampleInput(target = "Duck"))
        createNoteUseCase.execute("user-1", sampleInput(text = "Saw fresh tracks"))

        assertEquals(1, getNotesUseCase.execute("user-1", "northern").size)
        assertEquals(1, getNotesUseCase.execute("user-1", "duck").size)
        assertEquals(1, getNotesUseCase.execute("user-1", "tracks").size)
        assertEquals(3, getNotesUseCase.execute("user-1", "2026-05-28").size)
    }

    private fun sampleInput(
        dateTime: Instant = Instant.parse("2026-05-28T12:00:00Z"),
        location: String = "Field",
        target: String = "Boar",
        text: String = "Test note",
    ): NoteInput =
        NoteInput(
            dateTime = dateTime,
            location = location,
            target = target,
            text = text,
        )

    private class InMemoryNoteRepository : NoteRepository {
        private val notes = mutableListOf<Note>()

        override suspend fun findByUserId(userId: String, query: String?): List<Note> {
            val userNotes = notes.filter { it.userId == userId }
            val normalizedQuery = query?.lowercase()

            return if (normalizedQuery == null) {
                userNotes
            } else {
                userNotes.filter { note ->
                    note.location.contains(normalizedQuery, ignoreCase = true) ||
                        note.target.contains(normalizedQuery, ignoreCase = true) ||
                        note.text.contains(normalizedQuery, ignoreCase = true) ||
                        note.dateTime.toString().lowercase().contains(normalizedQuery)
                }
            }
        }

        override suspend fun findByIdForUser(userId: String, noteId: String): Note? =
            notes.firstOrNull { it.id == noteId && it.userId == userId }

        override suspend fun create(userId: String, input: NoteInput): Note {
            val now = Instant.parse("2026-05-28T13:00:00Z")
            val note = Note(
                id = "note-${notes.size + 1}",
                userId = userId,
                dateTime = input.dateTime,
                location = input.location,
                target = input.target,
                text = input.text,
                createdAt = now,
                updatedAt = now,
            )

            notes += note

            return note
        }

        override suspend fun update(userId: String, noteId: String, input: NoteInput): Note? {
            val index = notes.indexOfFirst { it.id == noteId && it.userId == userId }
            if (index == -1) {
                return null
            }

            val updated = notes[index].copy(
                dateTime = input.dateTime,
                location = input.location,
                target = input.target,
                text = input.text,
                updatedAt = Instant.parse("2026-05-28T14:00:00Z"),
            )
            notes[index] = updated

            return updated
        }

        override suspend fun delete(userId: String, noteId: String): Boolean =
            notes.removeIf { it.id == noteId && it.userId == userId }
    }
}
