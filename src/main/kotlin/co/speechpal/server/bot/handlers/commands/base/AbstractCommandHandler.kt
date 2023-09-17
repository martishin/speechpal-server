package co.speechpal.server.bot.handlers.commands.base

import arrow.core.Either
import arrow.core.raise.either
import co.speechpal.server.bot.errorhandling.ErrorHandler
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment

abstract class AbstractCommandHandler(protected val errorHandler: ErrorHandler) : CommandHandler {
    suspend fun handle(env: CommandHandlerEnvironment): Either<BotError, BotResponse> = either {
        val (bot, update, message, args) = env
        handle(bot, update, message, args).bind()
    }
}
