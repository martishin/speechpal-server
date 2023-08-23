package co.speechpal.server.common.models.domain

data class TextCheckResult(
    val report: List<SentenceCheckResult>,
    val hasErrors: Boolean,
)
