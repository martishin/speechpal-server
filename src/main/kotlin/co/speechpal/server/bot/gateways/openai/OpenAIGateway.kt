package co.speechpal.server.bot.gateways.openai

import co.speechpal.server.common.models.domain.SentenceCheckResult
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.Transcription
import java.io.File

@OptIn(BetaOpenAI::class)
interface OpenAIGateway {
    suspend fun transcribe(audioFile: File): Transcription
    suspend fun checkGrammar(sentence: String): SentenceCheckResult
}
