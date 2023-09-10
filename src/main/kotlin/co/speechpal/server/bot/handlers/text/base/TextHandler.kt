package co.speechpal.server.bot.handlers.text.base

import arrow.core.Either
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

interface TextHandler {
    suspend fun handle(bot: Bot, update: Update, message: Message, text: String): Either<BotError, BotResponse>
}
