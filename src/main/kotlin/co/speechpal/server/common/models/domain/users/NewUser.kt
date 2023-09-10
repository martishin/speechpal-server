package co.speechpal.server.common.models.domain.users

data class NewUser(
    val telegramUserId: Long,
    val chatId: Long,
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
)
