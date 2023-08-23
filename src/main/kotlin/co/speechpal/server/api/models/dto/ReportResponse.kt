package co.speechpal.server.api.models.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ReportResponse(
    val report: List<SentenceCheckResponse>,

    @JsonProperty("has_errors")
    val hasErrors: Boolean,
)
