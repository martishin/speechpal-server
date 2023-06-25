package co.speechpal.server

import co.speechpal.server.models.domain.Report
import co.speechpal.server.models.domain.Sentence
import co.speechpal.server.models.dto.ReportResponse
import co.speechpal.server.models.dto.SentenceResponse

fun Report.toResponse() =
    ReportResponse(
        report = this.report.map { it.toResponse() },
    )

fun Sentence.toResponse() =
    SentenceResponse(
        sentence = this.sentence,
        edit = this.edit,
    )
