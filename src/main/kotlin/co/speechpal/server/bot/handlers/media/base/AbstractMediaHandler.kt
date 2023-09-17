package co.speechpal.server.bot.handlers.media.base

import arrow.core.Either
import arrow.core.raise.either
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import com.github.kotlintelegrambot.dispatcher.handlers.media.MediaHandlerEnvironment

abstract class AbstractMediaHandler<Media> : MediaHandler<Media> {
    suspend fun handle(env: MediaHandlerEnvironment<Media>): Either<BotError, BotResponse> = either {
        val (bot, update, message, media) = env
        handle(bot, update, message, media).bind()
    }
}
