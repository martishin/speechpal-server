package co.speechpal.server.api.controllers

import co.speechpal.server.api.models.dto.ErrorResponse
import co.speechpal.server.api.toResponseEntity
import co.speechpal.server.common.services.reports.ReportsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    suspend fun findReportById(@PathVariable id: String): ResponseEntity<*> {
        return reportsService.findById(id).fold(
            { error ->
                ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, error.reason).toResponseEntity()
            },
            { report ->
                report?.toResponseEntity()
                    ?: ErrorResponse(HttpStatus.NOT_FOUND, "Report with id $id not found.").toResponseEntity()
            },
        )
    }
}
