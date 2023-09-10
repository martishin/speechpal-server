package co.speechpal.server.common.models.errors

sealed class DomainError(reason: String) : GenericError(reason) {
    class DatabaseError(reason: String) : DomainError(reason)
}
