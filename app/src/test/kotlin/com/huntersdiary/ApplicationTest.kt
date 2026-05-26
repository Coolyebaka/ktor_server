package com.huntersdiary

import com.huntersdiary.core.error.ValidationException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplicationTest {
    @Test
    fun `health endpoint returns ok`() = testApplication {
        application {
            module()
        }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"status":"ok"}""", response.body<String>())
    }

    @Test
    fun `application exceptions use api error format`() = testApplication {
        application {
            module()
            routing {
                get("/test-validation-error") {
                    throw ValidationException("Invalid test payload")
                }
            }
        }

        val response = client.get("/test-validation-error")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            """{"code":"VALIDATION_ERROR","message":"Invalid test payload"}""",
            response.body<String>(),
        )
    }
}
