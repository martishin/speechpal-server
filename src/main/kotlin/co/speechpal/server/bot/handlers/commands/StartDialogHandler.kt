package co.speechpal.server.bot.handlers.commands

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
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
    ): Either<BotError, BotResponse> = either {
        val telegramUserId = message.from?.id ?: return BotError.CannotReadUserData().left()

        val user = usersService.findByTelegramUserId(telegramUserId).bind() ?: raise(BotError.UserNotFound())

        if (user.currentDialogId != null && user.currentDialogId != 0) {
            raise(BotError.DialogAlreadyStarted())
        }

        dialogsService.create(NewDialog("gpt-3.5-turbo", user.id)).bind()

        BotResponse("Started a new dialog")
    }.mapLeft { error ->
        errorHandler.handleGenericError(error)
    }
}
