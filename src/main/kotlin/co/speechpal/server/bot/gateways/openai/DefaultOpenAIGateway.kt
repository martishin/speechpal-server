package co.speechpal.server.bot.gateways.openai

import arrow.core.Either
import arrow.core.raise.either
import co.speechpal.server.bot.configuration.BotProperties
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.models.domain.reports.SentenceCheckResult
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
import co.speechpal.server.bot.models.domain.ChatMessage as ChatMessageDomain
import co.speechpal.server.bot.models.domain.ChatRole as ChatRoleDomain

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

    override suspend fun transcribe(audioFile: File): Either<BotError, Transcription> = either {
        val request = TranscriptionRequest(
            audio = FileSource(
                path = audioFile.toOkioPath(),
                fileSystem = FileSystem.SYSTEM,
            ),
            model = ModelId("whisper-1"),
            language = "en",
        )

        openAIClient.transcription(request)
    }

    override suspend fun checkGrammar(sentence: String): Either<BotError, SentenceCheckResult> = either {
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

        var edit = completion.choices[0].message?.content ?: raise(BotError.OpenAiApiError())

        if ("Correct." in edit) {
            edit = ""
        }

        SentenceCheckResult(
            sentence = sentence,
            edit = edit,
        )
    }

    override suspend fun getChatCompletion(chat: List<ChatMessageDomain>): Either<BotError, String> = either {
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = chat.map { chatMessage ->
                ChatMessage(
                    role = chatMessage.role.toChatRole(),
                    content = chatMessage.message,
                )
            },
        )

        val completion = openAIClient.chatCompletion(request)

        completion.choices[0].message?.content ?: raise(BotError.OpenAiApiError())
    }

    private fun ChatRoleDomain.toChatRole() =
        when (this) {
            ChatRoleDomain.SYSTEM -> ChatRole.System
            ChatRoleDomain.BOT -> ChatRole.Assistant
            ChatRoleDomain.USER -> ChatRole.User
        }
}
