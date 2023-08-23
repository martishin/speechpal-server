package co.speechpal.server.common

import co.speechpal.server.common.models.domain.TextCheckResult
import co.speechpal.server.common.models.repository.Report

fun TextCheckResult.toReport(id: String) =
    Report(
        id = id,
        report = this.report,
        hasErrors = this.hasErrors,
    )
