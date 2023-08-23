package co.speechpal.server.bot.services.audio

import arrow.core.Either
import arrow.core.right
import co.speechpal.server.bot.gateways.openai.OpenAIGateway
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.dto.BotError
import com.aallam.openai.api.BetaOpenAI
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

@Service
@OptIn(BetaOpenAI::class)
class DefaultAudioTranscriberService(
    private val openAIClient: OpenAIGateway,
) : AudioTranscriberService {
    companion object {
        private val log = LoggerFactory.getLogger(DefaultAudioTranscriberService::class.java)
    }

    override suspend fun transcribe(context: Context, audioFile: File): Either<BotError, String> {
        val transcription = openAIClient.transcribe(audioFile)

        return transcription.text.right()
    }
}
