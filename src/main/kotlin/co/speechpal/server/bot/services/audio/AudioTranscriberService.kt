package co.speechpal.server.bot.services.audio

import arrow.core.Either
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.errors.BotError
import java.io.File

interface AudioTranscriberService {
    suspend fun transcribe(context: Context, audioFile: File): Either<BotError, String>
}
