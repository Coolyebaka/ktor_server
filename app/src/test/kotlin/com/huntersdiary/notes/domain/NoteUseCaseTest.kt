package com.huntersdiary.notes.domain

import com.huntersdiary.core.error.NotFoundException
import com.huntersdiary.core.error.ValidationException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class NoteUseCaseTest {
    @Test
    fun `create note stores note for user`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)

        val note = createNoteUseCase.execute("user-1", sampleInput(location = "Forest"))

        assertEquals("user-1", note.userId)
        assertEquals("Forest", note.location)
        assertEquals(note, repository.findByIdForUser("user-1", note.id))
    }

    @Test
    fun `create note allows empty date and time`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)

        val note = createNoteUseCase.execute(
            "user-1",
            sampleInput(date = null, time = null, location = null, target = null, text = null),
        )

        assertEquals(null, note.date)
        assertEquals(null, note.time)
        assertEquals(null, note.location)
        assertEquals(null, note.target)
        assertEquals(null, note.text)
    }

    @Test
    fun `create note accepts client technical timestamps`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val createdAt = Instant.parse("2026-05-27T10:00:00Z")
        val updatedAt = Instant.parse("2026-05-27T11:00:00Z")

        val note = createNoteUseCase.execute(
            "user-1",
            sampleInput(createdAt = createdAt, updatedAt = updatedAt),
        )

        assertEquals(createdAt, note.createdAt)
        assertEquals(updatedAt, note.updatedAt)
    }

    @Test
    fun `create note requires client technical timestamps`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)

        assertThrows(ValidationException::class.java) {
            runBlocking {
                createNoteUseCase.execute(
                    "user-1",
                    sampleInput(createdAt = null, updatedAt = Instant.parse("2026-05-27T11:00:00Z")),
                )
            }
        }
        assertThrows(ValidationException::class.java) {
            runBlocking {
                createNoteUseCase.execute(
                    "user-1",
                    sampleInput(createdAt = Instant.parse("2026-05-27T10:00:00Z"), updatedAt = null),
                )
            }
        }
    }

    @Test
    fun `get notes returns only own notes`() = runBlocking {
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
    fun `get note by id returns note only for owner`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val getNoteByIdUseCase = GetNoteByIdUseCase(repository)
        val note = createNoteUseCase.execute("owner", sampleInput())

        assertEquals(note, getNoteByIdUseCase.execute("owner", note.id))
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                getNoteByIdUseCase.execute("stranger", note.id)
            }
        }
    }

    @Test
    fun `update note only for owner`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val updateNoteUseCase = UpdateNoteUseCase(repository)
        val note = createNoteUseCase.execute("owner", sampleInput())

        val updated = updateNoteUseCase.execute("owner", note.id, sampleInput(location = "Updated"))

        assertEquals("Updated", updated.location)
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                updateNoteUseCase.execute("stranger", note.id, sampleInput(location = "Stranger update"))
            }
        }
        assertEquals("Updated", repository.findByIdForUser("owner", note.id)?.location)
    }

    @Test
    fun `update note requires client technical timestamps`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val updateNoteUseCase = UpdateNoteUseCase(repository)
        val note = createNoteUseCase.execute("owner", sampleInput())

        assertThrows(ValidationException::class.java) {
            runBlocking {
                updateNoteUseCase.execute(
                    "owner",
                    note.id,
                    sampleInput(createdAt = null, updatedAt = Instant.parse("2026-05-29T10:00:00Z")),
                )
            }
        }
        assertThrows(ValidationException::class.java) {
            runBlocking {
                updateNoteUseCase.execute(
                    "owner",
                    note.id,
                    sampleInput(createdAt = note.createdAt, updatedAt = null),
                )
            }
        }
    }

    @Test
    fun `delete note only for owner`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val deleteNoteUseCase = DeleteNoteUseCase(repository)
        val note = createNoteUseCase.execute("owner", sampleInput())

        assertThrows(NotFoundException::class.java) {
            runBlocking {
                deleteNoteUseCase.execute("stranger", note.id)
            }
        }

        assertEquals(note, repository.findByIdForUser("owner", note.id))
        deleteNoteUseCase.execute("owner", note.id)
        assertThrows(NotFoundException::class.java) {
            runBlocking {
                deleteNoteUseCase.execute("owner", note.id)
            }
        }
    }

    @Test
    fun `search checks location target text date and time strings`() = runBlocking {
        val repository = InMemoryNoteRepository()
        val createNoteUseCase = CreateNoteUseCase(repository)
        val getNotesUseCase = GetNotesUseCase(repository)

        createNoteUseCase.execute("user-1", sampleInput(location = "Northern forest"))
        createNoteUseCase.execute("user-1", sampleInput(target = "Duck"))
        createNoteUseCase.execute("user-1", sampleInput(text = "Saw fresh tracks"))
        createNoteUseCase.execute("user-1", sampleInput(time = LocalTime.parse("18:45:00")))

        assertEquals(1, getNotesUseCase.execute("user-1", "northern").size)
        assertEquals(1, getNotesUseCase.execute("user-1", "duck").size)
        assertEquals(1, getNotesUseCase.execute("user-1", "tracks").size)
        assertEquals(4, getNotesUseCase.execute("user-1", "2026-05-28").size)
        assertEquals(1, getNotesUseCase.execute("user-1", "18:45").size)
    }

    private fun sampleInput(
        date: LocalDate? = LocalDate.parse("2026-05-28"),
        time: LocalTime? = LocalTime.parse("12:00:00"),
        location: String? = "Field",
        target: String? = "Boar",
        text: String? = "Test note",
        createdAt: Instant? = Instant.parse("2026-05-28T13:00:00Z"),
        updatedAt: Instant? = Instant.parse("2026-05-28T13:00:00Z"),
    ): NoteInput =
        NoteInput(
            date = date,
            time = time,
            location = location,
            target = target,
            text = text,
            createdAt = createdAt,
            updatedAt = updatedAt,
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
                    note.location?.contains(normalizedQuery, ignoreCase = true) == true ||
                        note.target?.contains(normalizedQuery, ignoreCase = true) == true ||
                        note.text?.contains(normalizedQuery, ignoreCase = true) == true ||
                        note.date?.toString()?.lowercase()?.contains(normalizedQuery) == true ||
                        note.time?.toString()?.lowercase()?.contains(normalizedQuery) == true
                }
            }
        }

        override suspend fun findByIdForUser(userId: String, noteId: String): Note? =
            notes.firstOrNull { it.id == noteId && it.userId == userId }

        override suspend fun create(userId: String, input: NoteInput): Note {
            val note = Note(
                id = "note-${notes.size + 1}",
                userId = userId,
                date = input.date,
                time = input.time,
                location = input.location,
                target = input.target,
                text = input.text,
                createdAt = requireNotNull(input.createdAt),
                updatedAt = requireNotNull(input.updatedAt),
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
                date = input.date,
                time = input.time,
                location = input.location,
                target = input.target,
                text = input.text,
                createdAt = requireNotNull(input.createdAt),
                updatedAt = requireNotNull(input.updatedAt),
            )
            notes[index] = updated

            return updated
        }

        override suspend fun delete(userId: String, noteId: String): Boolean =
            notes.removeIf { it.id == noteId && it.userId == userId }
    }
}
