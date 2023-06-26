package co.speechpal.server.models.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ReportResponse(
    val report: List<SentenceResponse>,

    @JsonProperty("has_errors")
    val hasErrors: Boolean,
)
