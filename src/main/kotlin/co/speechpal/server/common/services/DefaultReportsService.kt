package co.speechpal.server.common.services

import co.speechpal.server.common.models.domain.TextCheckResult
import co.speechpal.server.common.models.repository.Report
import co.speechpal.server.common.repositories.ReportsRepository
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
