package co.speechpal.server.common.services.reports

import co.speechpal.server.common.models.domain.reports.TextCheckResult
import co.speechpal.server.common.models.persistence.Report

interface ReportsService {
    suspend fun findById(id: String): Report?
    suspend fun insertReport(id: String, textCheckResult: TextCheckResult)
}
