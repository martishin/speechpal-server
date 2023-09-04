package co.speechpal.server.common.repositories.reports

import co.speechpal.server.common.models.persistence.Report
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportsRepository : CoroutineCrudRepository<Report, String>
