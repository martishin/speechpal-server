package co.speechpal.server.bot.errorhandling

import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.models.errors.DomainError
import co.speechpal.server.common.models.errors.GenericError
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultErrorHandler : ErrorHandler {
    companion object {
        private val log = LoggerFactory.getLogger(DefaultErrorHandler::class.java)
    }

    override fun handleGenericError(error: GenericError): BotError {
        log.error("Error when processing bot request: ${error.reason}")

        return when (error) {
            is BotError -> error
            is DomainError -> BotError.DomainError(error.reason)
            else -> TODO()
        }
    }
}
