package co.speechpal.server.api.models.dto

data class SentenceCheckResponse(
    val sentence: String,
    val edit: String?,
)
