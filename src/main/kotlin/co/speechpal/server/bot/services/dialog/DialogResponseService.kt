package co.speechpal.server.bot.services.dialog

import arrow.core.Either
import co.speechpal.server.bot.models.errors.BotError

interface DialogResponseService {
    suspend fun getDialogResponse(dialog: List<String>): Either<BotError, String>
}
