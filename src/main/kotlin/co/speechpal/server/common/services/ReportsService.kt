package co.speechpal.server.common.services

import co.speechpal.server.common.models.domain.TextCheckResult
import co.speechpal.server.common.models.repository.Report

interface ReportsService {
    suspend fun findById(id: String): Report?
    suspend fun insertReport(id: String, textCheckResult: TextCheckResult)
}
