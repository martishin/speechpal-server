package co.speechpal.server.common.services.reports

import co.speechpal.server.common.models.domain.reports.TextCheckResult
import co.speechpal.server.common.models.persistence.Report
import co.speechpal.server.common.repositories.reports.ReportsRepository
import co.speechpal.server.common.toReport
import org.springframework.stereotype.Service

@Service
class DefaultReportsService(
    private val reportsRepository: ReportsRepository,
) : ReportsService {
    override suspend fun findById(id: String): Report? =
        reportsRepository.findById(id)

    override suspend fun insertReport(id: String, textCheckResult: TextCheckResult) {
        reportsRepository.save(textCheckResult.toReport(id))
    }
}
