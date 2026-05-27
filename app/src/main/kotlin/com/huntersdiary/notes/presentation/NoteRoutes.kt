package com.huntersdiary.notes.presentation

import com.huntersdiary.core.security.JwtService
import com.huntersdiary.core.security.requireUserId
import com.huntersdiary.notes.domain.CreateNoteUseCase
import com.huntersdiary.notes.domain.DeleteNoteUseCase
import com.huntersdiary.notes.domain.GetNoteByIdUseCase
import com.huntersdiary.notes.domain.GetNotesUseCase
import com.huntersdiary.notes.domain.UpdateNoteUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.noteRoutes(
    createNoteUseCase: CreateNoteUseCase,
    getNotesUseCase: GetNotesUseCase,
    getNoteByIdUseCase: GetNoteByIdUseCase,
    updateNoteUseCase: UpdateNoteUseCase,
    deleteNoteUseCase: DeleteNoteUseCase,
) {
    authenticate(JwtService.AUTH_PROVIDER) {
        route("/notes") {
            get {
                val userId = call.requireUserId()
                val query = call.request.queryParameters["query"]
                val notes = getNotesUseCase.execute(userId, query).map { it.toResponse() }

                call.respond(notes)
            }

            post {
                val userId = call.requireUserId()
                val request = call.receive<CreateNoteRequest>()
                val note = createNoteUseCase.execute(userId, request.toInput())

                call.respond(HttpStatusCode.Created, note.toResponse())
            }

            get("/{id}") {
                val userId = call.requireUserId()
                val noteId = call.parameters["id"].orEmpty()
                val note = getNoteByIdUseCase.execute(userId, noteId)

                call.respond(note.toResponse())
            }

            put("/{id}") {
                val userId = call.requireUserId()
                val noteId = call.parameters["id"].orEmpty()
                val request = call.receive<UpdateNoteRequest>()
                val note = updateNoteUseCase.execute(userId, noteId, request.toInput())

                call.respond(note.toResponse())
            }

            delete("/{id}") {
                val userId = call.requireUserId()
                val noteId = call.parameters["id"].orEmpty()

                deleteNoteUseCase.execute(userId, noteId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
