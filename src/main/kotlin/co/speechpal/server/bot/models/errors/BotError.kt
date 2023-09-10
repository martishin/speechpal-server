package co.speechpal.server.bot.models.errors

import co.speechpal.server.common.models.errors.GenericError

sealed class BotError(reason: String) : GenericError(reason) {
    class HandlerNotFound(reason: String) : BotError(reason)
    class CannotReadUserData : BotError("Cannot read telegram user data")
    class UserNotFound : BotError("User not found")
    class DialogAlreadyStarted : BotError("Dialog already started")
    class DialogWasNotStarted : BotError("Dialog wasn't started")
    class DialogNotFound : BotError("Dialog not found")
    class ErrorProcessingFile(reason: String) : BotError(reason)
    class ErrorConvertingAudioFile(reason: String) : BotError(reason)
    class DomainError(reason: String) : BotError(reason)
}
