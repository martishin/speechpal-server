package co.speechpal.server.bot.gateways.openai

import co.speechpal.server.bot.configuration.BotProperties
import co.speechpal.server.common.models.domain.SentenceCheckResult
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.Transcription
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.springframework.stereotype.Service
import java.io.File

@Service
@OptIn(BetaOpenAI::class)
class DefaultOpenAIGateway(
    botProperties: BotProperties,
) : OpenAIGateway {
    private val openAIClient = OpenAI(
        token = botProperties.openAIApiKey,
        logging = LoggingConfig(
            logLevel = LogLevel.None,
        ),
    )

    override suspend fun transcribe(audioFile: File): Transcription {
        val request = TranscriptionRequest(
            audio = FileSource(
                path = audioFile.toOkioPath(),
                fileSystem = FileSystem.SYSTEM,
            ),
            model = ModelId("whisper-1"),
            language = "en",
        )

        return openAIClient.transcription(request)
    }

    override suspend fun checkGrammar(sentence: String): SentenceCheckResult {
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = "Check the sentence and correct mistakes. Give the explanation. \"${sentence}\"." +
                        "If no correction is needed, return \"Correct.\"",
                ),
            ),
        )

        val completion = openAIClient.chatCompletion(request)

        var edit = completion.choices[0].message!!.content

        if (edit != null && "Correct." in edit) {
            edit = null
        }

        return SentenceCheckResult(
            sentence = sentence,
            edit = edit,
        )
    }
}
