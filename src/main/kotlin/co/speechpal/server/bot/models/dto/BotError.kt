package co.speechpal.server.bot.models.dto

sealed class BotError(val reason: String) {
    class HandlerNotFound(reason: String) : BotError(reason)
    class ErrorProcessingFile(reason: String) : BotError(reason)
    class ErrorConvertingAudioFile(reason: String) : BotError(reason)
}
