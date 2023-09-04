package co.speechpal.server.common.models.domain.reports

data class SentenceCheckResult(
    val sentence: String,
    val edit: String?,
)
