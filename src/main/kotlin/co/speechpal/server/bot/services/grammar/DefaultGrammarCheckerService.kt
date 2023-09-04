package co.speechpal.server.bot.services.grammar

import arrow.core.Either
import arrow.core.right
import co.speechpal.server.bot.gateways.openai.OpenAIGateway
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.dto.BotError
import co.speechpal.server.common.models.domain.reports.TextCheckResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class DefaultGrammarCheckerService(
    private val openAIClient: OpenAIGateway,
) : GrammarCheckerService {
    companion object {
        private val log = LoggerFactory.getLogger(DefaultGrammarCheckerService::class.java)
    }

    override suspend fun checkGrammar(context: Context, text: String): Either<BotError, TextCheckResult> {
        val sentences = Pattern.compile("(?<=[.!?])\\s+").split(text)

        val report = coroutineScope {
            sentences.map { sentence ->
                async { openAIClient.checkGrammar(sentence) }
            }.awaitAll().toMutableList()
        }

        return TextCheckResult(
            report = report,
            hasErrors = report.any { it.edit != null },
        ).right()
    }
}
