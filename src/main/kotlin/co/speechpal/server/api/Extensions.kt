package co.speechpal.server.api

import co.speechpal.server.api.models.dto.ReportResponse
import co.speechpal.server.api.models.dto.SentenceCheckResponse
import co.speechpal.server.common.models.domain.SentenceCheckResult
import co.speechpal.server.common.models.repository.Report

fun Report.toResponse() =
    ReportResponse(
        report = this.report.map { it.toResponse() },
        hasErrors = this.hasErrors,
    )

fun SentenceCheckResult.toResponse() =
    SentenceCheckResponse(
        sentence = this.sentence,
        edit = this.edit,
    )
