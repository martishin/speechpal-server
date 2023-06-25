package co.speechpal.server.repositories

import co.speechpal.server.models.domain.Report
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReportsRepository : CoroutineCrudRepository<Report, String>
