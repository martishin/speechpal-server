package co.speechpal.server.bot.handlers.commands.base

import arrow.core.Either
import co.speechpal.server.bot.models.dto.BotError
import co.speechpal.server.bot.models.dto.BotResponse
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment

abstract class AbstractCommandHandler : CommandHandler {
    suspend fun handle(env: CommandHandlerEnvironment): Either<BotError, BotResponse> {
        val (bot, update, message, args) = env
        return handle(bot, update, message, args)
    }
}
