package co.speechpal.server.bot.services.audio

import arrow.core.Either
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.dto.BotError
import java.io.File

interface AudioConverterService {
    suspend fun convert(context: Context, telegramAudioFile: File, audioFile: File): Either<BotError, Boolean>
}
