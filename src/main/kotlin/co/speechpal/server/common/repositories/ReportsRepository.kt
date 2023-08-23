package co.speechpal.server.common.repositories

import co.speechpal.server.common.models.repository.Report
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportsRepository : CoroutineCrudRepository<Report, String>
