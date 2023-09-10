package co.speechpal.server.bot.services.audio

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import co.speechpal.server.bot.gateways.openai.OpenAIGateway
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.errors.BotError
import com.aallam.openai.api.BetaOpenAI
import org.springframework.stereotype.Service
import java.io.File

@Service
@OptIn(BetaOpenAI::class)
class DefaultAudioTranscriberService(
    private val openAIClient: OpenAIGateway,
) : AudioTranscriberService {
    override suspend fun transcribe(context: Context, audioFile: File): Either<BotError, String> {
        return openAIClient.transcribe(audioFile).flatMap { transcription ->
            transcription.text.right()
        }
    }
}
