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
    override suspend fun handle(
        bot: Bot,
        update: Update,
        message: Message,
        args: List<String>,
    ): Either<BotError, BotResponse> {
        val telegramUserId = message.from?.id ?: return BotError.CannotReadUserData().left()

        return usersService.findByTelegramUserId(telegramUserId).flatMap { user ->
            if (user == null) {
                BotError.UserNotFound().left()
            } else {
                val updatedUser = user.copy(currentDialogId = null)
                usersService.save(updatedUser).flatMap {
                    BotResponse("Stopped the dialog").right()
                }
            }
        }.mapLeft { error ->
            errorHandler.handleGenericError(error)
        }
    }
}
