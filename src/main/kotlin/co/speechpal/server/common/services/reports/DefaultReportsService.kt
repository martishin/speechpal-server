package co.speechpal.server.common.services.reports

import arrow.core.Either
import arrow.core.right
import co.speechpal.server.common.models.domain.reports.TextCheckResult
import co.speechpal.server.common.models.errors.DomainError
import co.speechpal.server.common.models.persistence.Report
import co.speechpal.server.common.repositories.reports.ReportsRepository
import co.speechpal.server.common.toReport
import org.springframework.stereotype.Service

@Service
class DefaultReportsService(
    private val reportsRepository: ReportsRepository,
) : ReportsService {
    override suspend fun findById(id: String): Either<DomainError, Report?> =
        reportsRepository.findById(id).right()

    override suspend fun insertReport(id: String, textCheckResult: TextCheckResult): Either<DomainError, Report> =
        reportsRepository.save(textCheckResult.toReport(id)).right()
}
