package co.speechpal.server.bot.handlers.text

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import co.speechpal.server.bot.errorhandling.ErrorHandler
import co.speechpal.server.bot.handlers.Operation
import co.speechpal.server.bot.handlers.text.base.AbstractTextHandler
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.bot.services.dialog.DialogResponseService
import co.speechpal.server.common.services.dialogs.DialogsService
import co.speechpal.server.common.services.users.UsersService
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import org.springframework.stereotype.Component

@Component(Operation.TEXT)
class TextHandler(
    private val usersService: UsersService,
    private val dialogsService: DialogsService,
    private val dialogResponseService: DialogResponseService,
    errorHandler: ErrorHandler,
) : AbstractTextHandler(errorHandler) {
    override suspend fun handle(
        bot: Bot,
        update: Update,
        message: Message,
        text: String,
    ): Either<BotError, BotResponse> = either {
        val telegramUserId = message.from?.id ?: return BotError.CannotReadUserData().left()

        val user = usersService.findByTelegramUserId(telegramUserId).bind() ?: raise(BotError.UserNotFound())

        if (user.currentDialogId == null || user.currentDialogId == 0) {
            raise(BotError.DialogWasNotStarted())
        }

        val dialog = dialogsService.findById(user.currentDialogId).bind() ?: raise(BotError.DialogNotFound())

        val response = dialogResponseService.getDialogResponse(dialog.messages + text).bind()
        val updatedDialog = dialog.copy(messages = dialog.messages + text + response)

        dialogsService.save(updatedDialog).bind()

        BotResponse(response)
    }.mapLeft { error ->
        errorHandler.handleGenericError(error)
    }
}
