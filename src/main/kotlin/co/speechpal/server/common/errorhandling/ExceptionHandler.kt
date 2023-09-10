package co.speechpal.server.common.errorhandling

import co.speechpal.server.common.models.errors.DomainError

interface ExceptionHandler {
    fun handleDbException(e: Exception, message: String): DomainError
}
