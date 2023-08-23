package co.speechpal.server.bot.handlers.media.base

import arrow.core.Either
import co.speechpal.server.bot.models.dto.BotError
import co.speechpal.server.bot.models.dto.BotResponse
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

interface MediaHandler<Media> {
    suspend fun handle(bot: Bot, update: Update, message: Message, media: Media): Either<BotError, BotResponse>
}
