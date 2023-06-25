package co.speechpal.server.services

import co.speechpal.server.models.domain.Report
import co.speechpal.server.repositories.ReportsRepository
import org.springframework.stereotype.Service

@Service
class ReportsService(
    private val reportsRepository: ReportsRepository,
) {
    suspend fun findById(id: String): Report? =
        reportsRepository.findById(id)
}
