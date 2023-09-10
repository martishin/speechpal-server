package co.speechpal.server.bot.gateways.openai

import arrow.core.Either
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.models.domain.reports.SentenceCheckResult
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.Transcription
import java.io.File

@OptIn(BetaOpenAI::class)
interface OpenAIGateway {
    suspend fun transcribe(audioFile: File): Either<BotError, Transcription>
    suspend fun checkGrammar(sentence: String): Either<BotError, SentenceCheckResult>
    suspend fun getChatCompletion(dialog: List<String>): Either<BotError, String>
}
