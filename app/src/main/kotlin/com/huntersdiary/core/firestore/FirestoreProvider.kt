package com.huntersdiary.core.firestore

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.huntersdiary.core.config.FirestoreConfig
import java.io.Closeable
import java.io.FileInputStream

class FirestoreProvider(
    private val config: FirestoreConfig,
) : Closeable {
    @Volatile
    private var firestore: Firestore? = null

    fun get(): Firestore =
        firestore ?: synchronized(this) {
            firestore ?: createFirestore().also { firestore = it }
        }

    private fun createFirestore(): Firestore {
        val projectId = requireNotNull(config.projectId) {
            "FIRESTORE_PROJECT_ID is required to create Firestore client"
        }

        val builder = FirestoreOptions.newBuilder()
            .setProjectId(projectId)

        config.credentialsPath?.let { path ->
            FileInputStream(path).use { credentialsStream ->
                builder.setCredentials(GoogleCredentials.fromStream(credentialsStream))
            }
        }

        return builder.build().service
    }

    override fun close() {
        firestore?.close()
        firestore = null
    }
}
