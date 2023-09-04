package co.speechpal.server.common.models.domain

import java.time.Instant

data class Dialog(
    val id: Int,
    val messages: List<String>,
    val model: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val userId: Int,
)
