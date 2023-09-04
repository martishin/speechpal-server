package co.speechpal.server.api.controllers

import co.speechpal.server.api.models.dto.ReportResponse
import co.speechpal.server.api.models.errors.NotFoundException
import co.speechpal.server.api.toResponse
import co.speechpal.server.common.services.reports.ReportsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reports")
class ReportsController(
    private val reportsService: ReportsService,
) {
    @GetMapping("/{id}")
    suspend fun findReportById(@PathVariable id: String): ReportResponse {
        return reportsService.findById(id)?.toResponse()
            ?: throw NotFoundException(
                "Report with id $id not found.",
            )
    }
}
