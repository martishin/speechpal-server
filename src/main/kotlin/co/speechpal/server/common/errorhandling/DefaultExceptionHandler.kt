package co.speechpal.server.common.errorhandling

import co.speechpal.server.common.models.errors.DomainError
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultExceptionHandler : ExceptionHandler {
    companion object {
        private val log = LoggerFactory.getLogger(DefaultExceptionHandler::class.java)
    }

    override fun handleDbException(e: Exception, message: String): DomainError {
        log.error("$message: ${e.message}")
        return DomainError.DatabaseError(message)
    }
}
