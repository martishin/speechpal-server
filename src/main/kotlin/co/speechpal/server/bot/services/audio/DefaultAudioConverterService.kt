package co.speechpal.server.bot.services.audio

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.errors.BotError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.io.File

@Service
class DefaultAudioConverterService : AudioConverterService {
    override suspend fun convert(
        context: Context,
        telegramAudioFile: File,
        audioFile: File,
    ): Either<BotError, Boolean> {
        return withContext(Dispatchers.IO) {
            val command = listOf(
                "ffmpeg",
                "-i",
                telegramAudioFile.absolutePath,
                audioFile.absolutePath,
            )

            val process = ProcessBuilder(command).start()
            val processResultCode = process.waitFor()

            if (processResultCode != 0) {
                BotError.ErrorConvertingAudioFile("Error converting file").left()
            }

            true.right()
        }
    }
}
