package co.speechpal.server.common.models.domain.users

import java.time.Instant

data class User(
    val id: Int,
    val telegramUserId: Long,
    val chatId: Long,
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val currentDialogId: Int? = null,
)
