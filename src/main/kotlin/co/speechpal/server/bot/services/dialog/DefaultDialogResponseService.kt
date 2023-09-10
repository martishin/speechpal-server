package co.speechpal.server.bot.services.dialog

import arrow.core.Either
import co.speechpal.server.bot.gateways.openai.OpenAIGateway
import co.speechpal.server.bot.models.errors.BotError
import org.springframework.stereotype.Service

@Service
class DefaultDialogResponseService(private val openAIClient: OpenAIGateway) : DialogResponseService {
    override suspend fun getDialogResponse(dialog: List<String>): Either<BotError, String> {
        return openAIClient.getChatCompletion(dialog)
    }
}
