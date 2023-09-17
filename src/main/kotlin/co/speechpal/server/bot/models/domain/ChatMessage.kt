package co.speechpal.server.bot.models.domain

data class ChatMessage(
    val role: ChatRole,
    val message: String,
)
