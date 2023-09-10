package co.speechpal.server.bot.services.grammar

import arrow.core.Either
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.models.domain.reports.TextCheckResult

interface GrammarCheckerService {
    suspend fun checkGrammar(context: Context, text: String): Either<BotError, TextCheckResult>
}
