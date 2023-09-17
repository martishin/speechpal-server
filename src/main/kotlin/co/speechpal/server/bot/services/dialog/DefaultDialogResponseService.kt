package co.speechpal.server.bot.services.dialog

import arrow.core.Either
import arrow.core.raise.either
import co.speechpal.server.bot.gateways.openai.OpenAIGateway
import co.speechpal.server.bot.models.domain.ChatMessage
import co.speechpal.server.bot.models.domain.ChatRole
import co.speechpal.server.bot.models.errors.BotError
import org.springframework.stereotype.Service

@Service
class DefaultDialogResponseService(private val openAIClient: OpenAIGateway) : DialogResponseService {
    private companion object {
        const val SYSTEM_MESSAGE = """    
            As a chatbot primarily designed to help users enhance their English language skills, your primary duty is to promptly identify and correct any grammatical or spelling errors in the user's messages. Ensure to address these errors IMMEDIATELY in a polite and encouraging manner before progressing the conversation.
            
            After addressing any errors, continue to foster a vibrant and dynamic conversation by sharing brief insights, stories, or opinions without prompting. Maintain concise responses, typically about three sentences, to foster a natural and enjoyable dialogue flow. Feel free to initiate new topics or pose questions, encouraging a reciprocal conversation that extends beyond merely responding to the user's statements.
            
            Your overarching goal is to cultivate a nurturing and interactive atmosphere where users can effectively better their English language proficiency while feeling like they are chatting with a supportive friend.
        """
    }

    override suspend fun getDialogResponse(dialog: List<String>): Either<BotError, String> = either {
        val chat = List(dialog.size + 1) { index ->
            when (index) {
                0 -> ChatMessage(role = ChatRole.SYSTEM, message = SYSTEM_MESSAGE.trimIndent().trim())
                else -> {
                    val messageIndex = index - 1
                    val role = if (messageIndex % 2 == 0) ChatRole.BOT else ChatRole.USER
                    ChatMessage(role = role, message = dialog[messageIndex])
                }
            }
        }

        openAIClient.getChatCompletion(chat).bind()
    }
}
