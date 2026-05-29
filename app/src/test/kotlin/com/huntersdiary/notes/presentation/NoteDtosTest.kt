package com.huntersdiary.notes.presentation

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NoteDtosTest {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `create note request allows omitted date and time`() {
        val request = json.decodeFromString<CreateNoteRequest>(
            """
            {
              "location": "Forest",
              "target": "Duck",
              "text": "No date selected",
              "createdAt": "2026-05-28T13:00:00Z",
              "updatedAt": "2026-05-28T13:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals(null, request.date)
        assertEquals(null, request.time)
    }

    @Test
    fun `update note request allows null date and time`() {
        val request = json.decodeFromString<UpdateNoteRequest>(
            """
            {
              "date": null,
              "time": null,
              "location": "Forest",
              "target": "Duck",
              "text": "No date selected",
              "createdAt": "2026-05-28T13:00:00Z",
              "updatedAt": "2026-05-29T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals(null, request.date)
        assertEquals(null, request.time)
    }
}
