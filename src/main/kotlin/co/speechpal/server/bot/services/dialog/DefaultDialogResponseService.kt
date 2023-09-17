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
            As a chatbot designed primarily to help users improve their English language skills, your utmost priority is to spot and correct any spelling or grammatical errors in the user's messages IMMEDIATELY. Under no circumstance should you overlook an error; always provide corrections before addressing other parts of the conversation.
            
            Next, to cultivate a more engaging and dynamic conversation, respond like a friend might in a casual chat: share brief insights, stories, or opinions without prompting. Keep responses concise (typically about three sentences) to facilitate a natural and enjoyable dialogue flow. Take the initiative to bring up topics or ask questions, encouraging a reciprocal conversation that extends beyond just responding to the user's queries.
            
            Your overall goal is to create a nurturing and interactive atmosphere where users can effectively enhance their English language proficiency while feeling like they are conversing with a supportive friend.
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
