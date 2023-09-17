package co.speechpal.server.bot.handlers.commands

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import co.speechpal.server.bot.errorhandling.ErrorHandler
import co.speechpal.server.bot.handlers.Operation
import co.speechpal.server.bot.handlers.commands.base.AbstractCommandHandler
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.services.users.UsersService
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import org.springframework.stereotype.Component

@Component(Operation.STOP_DIALOG)
class StopDialogHandler(
    private val usersService: UsersService,
    errorHandler: ErrorHandler,
) : AbstractCommandHandler(errorHandler) {
    private companion object {
        const val LAST_MESSAGE = "Bye, see you around!"
    }

    override suspend fun handle(
        bot: Bot,
        update: Update,
        message: Message,
        args: List<String>,
    ): Either<BotError, BotResponse> = either {
        val telegramUserId = message.from?.id ?: return BotError.CannotReadUserData().left()

        val user = usersService.findByTelegramUserId(telegramUserId).bind() ?: raise(BotError.UserNotFound())

        val updatedUser = user.copy(currentDialogId = null)
        usersService.save(updatedUser).bind()

        BotResponse(LAST_MESSAGE)
    }.mapLeft { error ->
        errorHandler.handleGenericError(error)
    }
}
