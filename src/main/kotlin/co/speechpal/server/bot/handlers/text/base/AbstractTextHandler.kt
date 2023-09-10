package co.speechpal.server.bot.handlers.text.base

import arrow.core.Either
import co.speechpal.server.bot.errorhandling.ErrorHandler
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment

abstract class AbstractTextHandler(protected val errorHandler: ErrorHandler) : TextHandler {
    suspend fun handle(env: TextHandlerEnvironment): Either<BotError, BotResponse> {
        val (bot, update, message, text) = env
        return handle(bot, update, message, text)
    }
}
