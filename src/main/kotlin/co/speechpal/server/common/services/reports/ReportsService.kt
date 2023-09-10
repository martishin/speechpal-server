package co.speechpal.server.common.services.reports

import arrow.core.Either
import co.speechpal.server.common.models.domain.reports.TextCheckResult
import co.speechpal.server.common.models.errors.DomainError
import co.speechpal.server.common.models.persistence.Report

interface ReportsService {
    suspend fun findById(id: String): Either<DomainError, Report?>
    suspend fun insertReport(id: String, textCheckResult: TextCheckResult): Either<DomainError, Report>
}
