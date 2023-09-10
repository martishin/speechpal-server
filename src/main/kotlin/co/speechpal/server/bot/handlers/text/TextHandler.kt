package co.speechpal.server.bot.handlers.text

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
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
    ): Either<BotError, BotResponse> {
        val telegramUserId = message.from?.id ?: return BotError.CannotReadUserData().left()

        return usersService.findByTelegramUserId(telegramUserId).flatMap { user ->
            when {
                user == null -> BotError.UserNotFound().left()
                user.currentDialogId == null || user.currentDialogId == 0 -> BotError.DialogWasNotStarted().left()
                else -> dialogsService.findById(user.currentDialogId).flatMap { dialog ->
                    if (dialog == null) {
                        BotError.DialogNotFound().left()
                    } else {
                        dialogResponseService.getDialogResponse(dialog.messages + text).flatMap { response ->
                            dialogsService.save(dialog.copy(messages = dialog.messages + text + response)).flatMap {
                                BotResponse(response).right()
                            }
                        }
                    }
                }
            }
        }.mapLeft { error ->
            errorHandler.handleGenericError(error)
        }
    }
}
