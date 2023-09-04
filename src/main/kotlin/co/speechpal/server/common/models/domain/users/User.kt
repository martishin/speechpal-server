package co.speechpal.server.common.models.domain.users

import java.time.Instant

data class User(
    val id: Int? = null,
    val telegramUserId: Long,
    val chatId: Long,
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val currentDialogId: Int? = null,
)
