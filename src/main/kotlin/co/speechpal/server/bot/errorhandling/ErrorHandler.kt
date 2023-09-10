package co.speechpal.server.bot.errorhandling

import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.models.errors.GenericError

interface ErrorHandler {
    fun handleGenericError(error: GenericError): BotError
}
