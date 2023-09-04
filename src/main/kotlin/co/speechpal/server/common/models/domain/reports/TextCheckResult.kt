package co.speechpal.server.common.models.domain.reports

data class TextCheckResult(
    val report: List<SentenceCheckResult>,
    val hasErrors: Boolean,
)
