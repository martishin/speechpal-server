package co.speechpal.server.api

import co.speechpal.server.api.models.dto.ErrorResponse
import co.speechpal.server.api.models.dto.ReportResponse
import co.speechpal.server.api.models.dto.SentenceCheckResponse
import co.speechpal.server.common.models.domain.reports.SentenceCheckResult
import co.speechpal.server.common.models.persistence.Report
import org.springframework.http.ResponseEntity

fun Report.toResponseEntity() =
    ResponseEntity.ok(
        ReportResponse(
            report = this.report.map { it.toResponse() },
            hasErrors = this.hasErrors,
        ),
    )

fun SentenceCheckResult.toResponse() =
    SentenceCheckResponse(
        sentence = this.sentence,
        edit = this.edit,
    )

fun ErrorResponse.toResponseEntity() =
    ResponseEntity.status(this.httpStatus).body(this)
