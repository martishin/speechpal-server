package co.speechpal.server.bot.services.telegram

import arrow.core.Either
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.domain.TelegramFile
import co.speechpal.server.bot.models.errors.BotError
import com.github.kotlintelegrambot.Bot

interface TelegramFileService {
    suspend fun downloadFileById(context: Context, bot: Bot, fileId: String): Either<BotError, TelegramFile>
}
