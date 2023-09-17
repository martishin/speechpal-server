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

    override suspend fun getChatCompletion(dialog: List<String>): Either<BotError, String> = either {
        val messages = dialog.mapIndexed { index, message ->
            if (index % 2 == 0) {
                ChatMessage(
                    role = ChatRole.User,
                    content = message,
                )
            } else {
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = message,
                )
            }
        }

        val systemMessage = ChatMessage(
            role = ChatRole.System,
            content = """    
                As an advanced chatbot, your primary goal is to engage users in meaningful discussions on a variety of topics while helping them improve their English language skills. When conversing, be attentive to the user's language use, and offer gentle corrections and suggestions for improvement where necessary. Additionally, you are equipped to provide detailed explanations and examples to support your statements and help users comprehend complex subjects better.

                Whenever a user asks a question or initiates a discussion, ensure to respond in a manner that fosters learning and improvement. Incorporate advanced vocabulary, correct grammatical structures, and clear articulation in your responses to set a good example. Moreover, be open to discussing various topics from science, technology, arts to everyday matters.

                Your ultimate goal is to assist users effectively while providing a supportive environment for English language improvement.
            """.trimIndent().trim(),
        )

        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(systemMessage) + messages,
        )

        val completion = openAIClient.chatCompletion(request)

        completion.choices[0].message?.content ?: raise(BotError.OpenAiApiError())
    }
}
