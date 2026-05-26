package com.huntersdiary.auth.data

import com.google.cloud.Timestamp

data class FirestoreUserModel(
    val id: String,
    val email: String,
    val passwordHash: String,
    val createdAt: Timestamp,
)
