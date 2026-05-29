package com.huntersdiary.notes.data

import com.google.cloud.Timestamp
import com.google.cloud.firestore.DocumentSnapshot
import com.huntersdiary.notes.domain.Note
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun FirestoreNoteModel.toDomain(): Note =
    Note(
        id = id,
        userId = userId,
        date = date?.let(LocalDate::parse),
        time = time?.let(LocalTime::parse),
        location = location,
        target = target,
        text = text,
        createdAt = createdAt.toKotlinInstant(),
        updatedAt = updatedAt.toKotlinInstant(),
    )

fun DocumentSnapshot.toFirestoreNoteModel(): FirestoreNoteModel? {
    val data = data ?: return null

    return FirestoreNoteModel(
        id = data["id"] as? String ?: id,
        userId = data["userId"] as? String ?: return null,
        date = data["date"] as? String,
        time = data["time"] as? String,
        location = data["location"] as? String,
        target = data["target"] as? String,
        text = data["text"] as? String,
        createdAt = data["createdAt"] as? Timestamp ?: return null,
        updatedAt = data["updatedAt"] as? Timestamp ?: return null,
    )
}

fun FirestoreNoteModel.toFirestoreMap(): Map<String, Any?> =
    mapOf(
        "id" to id,
        "userId" to userId,
        "date" to date,
        "time" to time,
        "location" to location,
        "target" to target,
        "text" to text,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
    )

fun Instant.toFirestoreTimestamp(): Timestamp =
    Timestamp.ofTimeSecondsAndNanos(epochSeconds, nanosecondsOfSecond)

fun Timestamp.toKotlinInstant(): Instant =
    Instant.fromEpochSeconds(seconds, nanos.toLong())
