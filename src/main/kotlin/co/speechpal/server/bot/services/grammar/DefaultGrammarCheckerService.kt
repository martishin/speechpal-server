package co.speechpal.server.bot.services.grammar

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import co.speechpal.server.bot.gateways.openai.OpenAIGateway
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.models.domain.reports.SentenceCheckResult
import co.speechpal.server.common.models.domain.reports.TextCheckResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class DefaultGrammarCheckerService(private val openAIClient: OpenAIGateway) : GrammarCheckerService {
    override suspend fun checkGrammar(context: Context, text: String): Either<BotError, TextCheckResult> = either {
        val sentences = Pattern.compile("(?<=[.!?])\\s+").split(text)

        val checks = coroutineScope {
            sentences.map { sentence ->
                async { openAIClient.checkGrammar(sentence) }
            }.awaitAll().toMutableList()
        }

        val report = mutableListOf<SentenceCheckResult>()
        for (check in checks) {
            check.fold(
                { return it.left() },
                { report.add(it) },
            )
        }

        TextCheckResult(
            report = report,
            hasErrors = report.any { it.edit != null },
        )
    }
}
