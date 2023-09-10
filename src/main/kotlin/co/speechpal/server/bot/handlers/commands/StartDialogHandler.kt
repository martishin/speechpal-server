package co.speechpal.server.bot.handlers.commands

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.speechpal.server.bot.errorhandling.ErrorHandler
import co.speechpal.server.bot.handlers.Operation
import co.speechpal.server.bot.handlers.commands.base.AbstractCommandHandler
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.models.domain.dialogs.NewDialog
import co.speechpal.server.common.services.dialogs.DialogsService
import co.speechpal.server.common.services.users.UsersService
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import org.springframework.stereotype.Component

@Component(Operation.START_DIALOG)
class StartDialogHandler(
    private val usersService: UsersService,
    private val dialogsService: DialogsService,
    errorHandler: ErrorHandler,
) : AbstractCommandHandler(errorHandler) {
    override suspend fun handle(
        bot: Bot,
        update: Update,
        message: Message,
        args: List<String>,
    ): Either<BotError, BotResponse> {
        val telegramUserId = message.from?.id ?: return BotError.CannotReadUserData().left()

        return usersService.findByTelegramUserId(telegramUserId).flatMap { user ->
            when {
                user == null -> BotError.UserNotFound().left()
                user.currentDialogId != null && user.currentDialogId != 0 -> BotError.DialogAlreadyStarted().left()
                else -> dialogsService.create(NewDialog("gpt-3.5-turbo", user.id)).flatMap {
                    BotResponse("Started a new dialog").right()
                }
            }
        }.mapLeft { error ->
            errorHandler.handleGenericError(error)
        }
    }
}
